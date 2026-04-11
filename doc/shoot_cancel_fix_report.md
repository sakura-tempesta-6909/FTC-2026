# シューターキャンセル不具合 — 原因と修正の記録

## 発端

TeleOp で「X ボタンを押している間だけシューターを回し、離したら止める」操作を実装したが、**ボタンを離してもフィーダー/インテークが止まらない**不具合が発生した。

---

## 原因 (3 層にわたる問題)

### 原因 1: Subsystem が state machine 型だった

```java
// 旧設計: Subsystem 内に state 変数を持ち、periodic() で毎ループ適用
public enum IntakeState { INTAKE, STOP, REVERSE }
private IntakeState state = STOP;

@Override public void periodic() {
    switch (state) {
        case INTAKE  -> motor.setPower(1.0);
        case STOP    -> motor.setPower(0.0);
    }
}
```

**問題**: Command が終了しても `state` フィールドが `INTAKE` のまま残る。`periodic()` が毎ループ state を読んでモータを回し続けるため、Command のライフサイクルと Subsystem の状態が乖離する。

### 原因 2: feedAndIntake が即完了して SequentialGroup から消えていた

```java
// 旧設計: 状態をセットして即 isDone=true
new LambdaCommand()
    .setStart(() -> {
        IntakeSubsystem.INSTANCE.setState(IntakeState.INTAKE);
        FeederSubsystem.INSTANCE.setState(FeederState.FEED);
    })
    .setIsDone(() -> true)   // ← 1フレームで完了
```

**問題**: `isDone=true` で即完了 → SequentialGroup の children deque から消える → `runningCommands` からも SequentialGroup 自体が消える。ボタン離下時に `stopAll` を schedule しても、競合検出の対象が存在しない → キャンセルが空振り。

### 原因 3: spinUp に setStop がなかった

```java
// 旧設計: setStop なし
new LambdaCommand()
    .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
    .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
    // setStop なし → 中断されても target velocity が残る
```

**問題**: spinUp が外部からキャンセルされても target velocity がリセットされない。shooter モータが回り続ける。

---

## 修正内容

### 修正 A: Subsystem を「薄いラッパー」に変更

state machine を廃止し、`setPower()` / `stop()` だけの直接操作型にした。

```java
// 新設計
public class IntakeSubsystem implements Subsystem {
    private MotorEx motor;
    
    @Override public void initialize() {
        motor = new MotorEx(Const.Intake.Motor.NAME);
        motor.brakeMode();
        motor.reverse();
        motor.setPower(Const.Intake.Power.STOP);
    }
    
    // periodic() なし — Command が直接 setPower を呼ぶ
    
    public void setPower(double power) { motor.setPower(power); }
    public void stop() { motor.setPower(Const.Intake.Power.STOP); }
}
```

**効果**: Subsystem は状態を持たない。モータの状態は「今 running な Command が何を呼んだか」だけで決まる。Command が終了して何も呼ばなくなれば、モータは最後に呼ばれた値のまま → Command の `setStop` で明示的に `stop()` を呼ぶ責任がある。

### 修正 B: Command を「永続 + setStop」パターンに統一

全ての副作用コマンドを永続化 (`setIsDone(() -> false)`) し、`setStop` で Subsystem を停止するようにした。

```java
// 新設計
public static Command intake() {
    return new LambdaCommand()
        .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.INTAKE))
        .setIsDone(() -> false)                                   // 永続
        .setStop(interrupted -> IntakeSubsystem.INSTANCE.stop())  // 停止保証
        .addRequirements(IntakeSubsystem.INSTANCE)
        .named("intake");
}
```

**効果**: Command が `runningCommands` に居続けるため、ボタン離下 → `cancel()` → `stop(true)` が確実に伝播する。

### 修正 C: spinUp に setStop を追加 + holdRpm を新設

```java
public static Command spinUp() {
    return new LambdaCommand()
        .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
        .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
        .setStop(interrupted -> {
            if (interrupted) ShooterSubsystem.INSTANCE.stop();  // 中断時のみ停止
        })
        .addRequirements(ShooterSubsystem.INSTANCE)
        .named("spinUp");
}

// spinUp が自然完了した後の「Shooter のオーナー」
public static Command holdRpm() {
    return new LambdaCommand()
        .setIsDone(() -> false)                                      // 永続
        .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())    // 無条件停止
        .addRequirements(ShooterSubsystem.INSTANCE)
        .named("holdRpm");
}
```

**効果**: spinUp は目標到達後に自然完了する。holdRpm が連射フェーズで Shooter の running owner として ParallelGroup 内に残り、キャンセル時に stop(true) が走る。

### 修正 D: Command の責務を単一 Subsystem に分離 + Routine 層を新設

旧 `ShooterCommand` は Shooter / Feeder / Intake の 3 Subsystem を直接操作していた。これを分離:

```
command/
  ShooterCommand.java  ← Shooter のみ (spinUp, spinUpReverse, holdRpm)
  FeederCommand.java   ← Feeder のみ (feed, weakFeed, retract, retractFor)
  IntakeCommand.java   ← Intake のみ (intake, outtake)

routine/
  ShootingRoutine.java ← 複数 Subsystem の Command を SequentialGroup/ParallelGroup で合成
  IntakeRoutine.java   ← 同上
```

**効果**: 各 Command が 1 Subsystem だけを requires する。Routine の `ParallelGroup` 内で並列に動く leaf が個別に `setStop` で後始末するため、どのフェーズでキャンセルされても安全。

### 修正 E: spinUpReverse を永続化

```java
// 旧: isDone = isAtVelocity → 目標到達で自然完了 → cancel が空振り
// 新: isDone = false → 永続 → cancel で確実に停止
public static Command spinUpReverse() {
    return new LambdaCommand()
        .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
        .setIsDone(() -> false)                                      // 永続に変更
        .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())
        .addRequirements(ShooterSubsystem.INSTANCE)
        .named("spinUpReverse");
}
```

---

## 追加で発見した NextFTC のバグ (2 件)

### NextFTC バグ 1: LambdaCommand.requires() の Kotlin spread 不具合

`LambdaCommand.requires(vararg requirements: Any)` のオーバーライドで、Kotlin の spread operator `*requirements` がバイトコード上で **配列を spread せず新しい配列に包んで親に渡す** コードを生成していた。結果として `Set<Any>` に個別オブジェクトではなく `Object[]` 配列自体が 1 要素として追加される。

```java
// ❌ 壊れている (LambdaCommand のオーバーライド経由)
.requires(IntakeSubsystem.INSTANCE)
// → Set の中身: [Object[]{IntakeSubsystem.INSTANCE}]  ← 配列が入る

// ✅ 回避策 (Command の final メソッドを直接使う)
.addRequirements(IntakeSubsystem.INSTANCE)
// → Set の中身: [IntakeSubsystem.INSTANCE]  ← 正しい
```

### NextFTC バグ 2: ParallelRaceGroup の endAfter が永続コマンドに効かない

`ParallelRaceGroup` は `ParallelGroup` を継承している。`ParallelGroup.update()` は完了した子を `children` deque から削除する。その後 `ParallelRaceGroup.isDone()` が children を走査しても、削除済みの子は見つからない。

```
endAfter(0.2) → ParallelRaceGroup(retract, Delay(0.2))

1. update(): Delay が isDone → Delay を children から削除
2. isDone(): children = [retract] だけ → retract は永続 → false
→ ParallelRaceGroup は永遠に完了しない
```

回避策: `endAfter` の代わりに自前タイマー (`retractFor`) または `ParallelDeadlineGroup` を使う。`ParallelDeadlineGroup.isDone()` は deadline コマンドの isDone だけを見るため、children 削除の影響を受けない。

---

## 修正後の Routine 構造

```java
public static Command shootWithRetract() {
    return new SequentialGroup(
        FeederCommand.retractFor(0.2),        // 自前タイマーで 0.2 秒引き戻し
        ShooterCommand.spinUp(),              // 目標 RPM 到達まで待機
        new ParallelGroup(                    // 連射フェーズ (全て永続)
            ShooterCommand.holdRpm(),         //   Shooter 維持
            IntakeCommand.intake(),           //   Intake 動作
            FeederCommand.feed()              //   Feeder 送り込み
        )
    ).named("shootWithRetract");
}
```

### キャンセル時の動作保証

| 中断タイミング | stop(true) の対象 | 結果 |
|---|---|---|
| retractFor 中 | retractFor → setStop で feeder.stop() | ✅ Feeder 停止 |
| spinUp 中 | spinUp → setStop で shooter.stop() (interrupted=true) | ✅ Shooter 停止 |
| 連射中 | ParallelGroup → holdRpm, intake, feed 全ての setStop | ✅ 全 Subsystem 停止 |

---

## TeleOp のバインディング

```java
Command shootCmd = ShootingRoutine.shootWithRetract();
Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
        .whenBecomesTrue(shootCmd::schedule)
        .whenBecomesFalse(shootCmd::cancel);
```

`cancel()` → `CommandManager.cancelCommand` → `stop(true)` → SequentialGroup が現在の child に伝播 → leaf の setStop で Subsystem 停止。

---

## テスト

mockito-inline ベースの 48 ユニットテストで以下を検証済み:

- Subsystem: setPower / stop が正しいパワーを送る
- Command: requires / interruptible / name のメタ情報、start/stop の副作用
- Routine: 連射フェーズでキャンセル → intake/feeder が STOP に戻る (リグレッションテスト)
- Const: 距離→RPM の段階切り替えと境界値
