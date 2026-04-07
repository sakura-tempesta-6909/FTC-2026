# ShooterCommand キャンセル不具合 — 解析資料

> このドキュメントは別の AI/開発者へ引き継ぐための自己完結型の資料です。前提知識なしで読めるように書いています。

## 0. 一行サマリ

「X ボタンを押している間だけシューターを回し、離したら止める」というテレオペ操作で、*
*ボタンを離してもシューター/フィーダー/インテークが止まらない（キャンセルが効かない）** 不具合が出ている。

---

## 1. 環境

- **大会**: FIRST Tech Challenge 2026 シーズン
- **言語**: Java (FTC SDK 11.0.0)
- **フレームワーク**: NextFTC (`dev.nextftc:ftc:1.0.1`, `core:1.0.1`, `hardware:1.0.1`, `control:1.0.0`)、コマンドベース構成
- **拡張**: Pedro Pathing (`com.pedropathing:ftc:2.0.4`)、NextFTC Pedro extension (`dev.nextftc.extensions:pedro:1.0.0`)
- **テレメトリ**: Panels (`com.bylazar:fullpanels:1.0.12`)
- **ハードウェア**: Mecanum 4輪 + Limelight 3A + REV IMU + goBILDA Pinpoint + ShooterMotor / FeederMotor / IntakeMotor

---

## 2. NextFTC のコマンドモデル（必要最小限）

### 2.1 Subsystem

- シングルトン (`INSTANCE`) パターン
- `initialize()` でハードウェア初期化、`periodic()` を毎ループ呼ぶ
- 内部で `state` 変数を持ち、`periodic()` がその state に応じてモータ出力を決める「ステートマシン型」

例: `IntakeSubsystem` は `IntakeState ∈ {INTAKE, STOP, REVERSE}` を持ち、`periodic()` で switch して `setPower(...)`
する。Command は `setState(...)` を呼ぶだけ。

### 2.2 Command と LambdaCommand

- `Command implements Runnable`
- ライフサイクル: `start()` → 毎ループ `update()` + `isDone()` 判定 → 終了時 `stop(interrupted)`
- `requires(...)` でリソース宣言（競合検出用）
- `setInterruptible(true/false)` で中断可否
- LambdaCommand: `.setStart(r).setUpdate(r).setIsDone(s).setStop(c).requires(...).named(...)` のチェーン形式
- **`Command.run()` は内部で `schedule()` を呼ぶ**（つまり Command を Runnable として渡すと、毎回 schedule される）

### 2.3 SequentialGroup / ParallelGroup

- `SequentialGroup(a, b, c)` は `a → b → c` と順次実行
- 内部に `ArrayDeque<Command> children` を持ち、完了した子を `removeFirst()` していく
- **CommandGroup コンストラクタは子の `requirements` を集約して自身に `setRequirements(...)` する**
- `CommandGroup.stop()` は children deque をクリアして元のコマンドを再追加（リセット）

### 2.4 CommandManager（公式ソース）

```kotlin
fun run() {
    for (command in runningCommands) {
        command.update()
        if (command.isDone) {
            commandsToCancel += Pair(command, false)
        }
    }
    scheduleCommands()   // → initCommand を呼ぶ
    cancelCommands()     // → cancel() を呼ぶ
}

private fun initCommand(command: Command) {
    val requirements = expandRequirements(command.requirements)
    for (otherCommand in runningCommands) {
        val otherRequirements = expandRequirements(otherCommand.requirements)
        for (requirement in requirements) {
            if (otherRequirements.contains(requirement)) {
                if (otherCommand.interruptible) {
                    commandsToCancel += Pair(otherCommand, true)  // 中断マーク
                } else {
                    return                                          // ★silently abort★
                }
            }
        }
    }
    command.start()
    runningCommands += command
}
```

**ポイント**:

1. `run()` の順序は **`update → schedule → cancel`**
2. `initCommand` は競合する running command が `interruptible == true` ならキャンセルマーク、`false` なら **新コマンドを
   silently drop**（start も add もされない、cancelToCancel にも何も入れない）
3. 競合検出は requirements 集合の intersection

### 2.5 NextBindings（ボタン）

- `Button.whenBecomesTrue(Runnable)` / `whenBecomesFalse(Runnable)` で立ち上がり/立ち下がりエッジに Runnable を登録
- BindingsComponent.preUpdate がボタン状態を読みエッジを判定して登録された Runnable を呼ぶ
- `Command implements Runnable` なので Command を直接渡せる（毎回 `command.run() = command.schedule()` が呼ばれる）

### 2.6 OpMode のループ順序

`addComponents` の登録順は `Pedro → Subsystem → BulkRead → Bindings`。`CommandManager` は自動登録。

1ループ内の実行順:

```
preUpdate (登録順):
  PedroComponent.preUpdate
  SubsystemComponent.preUpdate  → 全 Subsystem の periodic() を呼ぶ（モータ出力反映）
  BulkReadComponent.preUpdate
  BindingsComponent.preUpdate   → ボタン状態更新 + コールバック実行（schedule が走る）

userOpMode.onUpdate              → ユーザコード

postUpdate (登録順の逆):
  ... 各種 postUpdate
  CommandManager.postUpdate     → run() 実行
                                    1. 全running の update() + isDone判定
                                    2. scheduleCommands → initCommand
                                    3. cancelCommands
```

---

## 3. ユーザのコード（該当部分）

### 3.1 `subsystem/ShooterSubsystem.java`（要点）

```java

@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();
    private MotorEx shooterMotor;
    private ControlSystem controller;          // velocity PID
    private double targetVelocity = 0.0;

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.brakeMode();
        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        controller.setGoal(new KineticState(0.0, 0.0));
        // limelight, imu 初期化省略
    }

    @Override
    public void periodic() {
        double power = controller.calculate(new KineticState(0.0, shooterMotor.getVelocity()));
        shooterMotor.setPower(power);
        // limelight 距離計算で distance 更新
    }

    public void setTargetVelocity(double v) {
        this.targetVelocity = v;
        controller.setGoal(new KineticState(0.0, v));
    }

    public void setTargetRPM() { /* distance 値で 1100 / 1300 / 1400 / 1500 を切り替え */ }

    public void stop() {
        setTargetVelocity(0.0);
    }

    public boolean isAtVelocity() {
        return Math.abs(shooterMotor.getVelocity() - controller.getGoal().getVelocity()) <= TOLERANCE;
    }
}
```

### 3.2 `subsystem/IntakeSubsystem.java`（全体）

```java
public class IntakeSubsystem implements Subsystem {
    public enum IntakeState {INTAKE, STOP, REVERSE}

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();
    private MotorEx intakeMotor;
    private IntakeState state = IntakeState.STOP;

    public void setState(IntakeState state) {
        this.state = state;
    }

    @Override
    public void initialize() {
        intakeMotor = new MotorEx(Const.Intake.Motor.NAME);
        intakeMotor.brakeMode();
        intakeMotor.reverse();
        state = IntakeState.STOP;
    }

    @Override
    public void periodic() {
        switch (state) {
            case INTAKE -> intakeMotor.setPower(1.0);
            case REVERSE -> intakeMotor.setPower(-1.0);
            case STOP -> intakeMotor.setPower(0.0);
        }
    }
}
```

### 3.3 `subsystem/FeederSubsystem.java`（全体）

```java
public class FeederSubsystem implements Subsystem {
    public enum FeederState {FEED, STOP, WEAKFEED, RETRACT}

    public static final FeederSubsystem INSTANCE = new FeederSubsystem();
    private MotorEx feederMotor;
    private FeederState state = FeederState.STOP;

    public void setState(FeederState state) {
        this.state = state;
    }

    @Override
    public void initialize() { /* motor reverse + brakeMode + STOP */ }

    @Override
    public void periodic() {
        switch (state) {
            case FEED -> feederMotor.setPower(1.0);
            case WEAKFEED -> feederMotor.setPower(0.3);
            case RETRACT -> feederMotor.setPower(-1.0);
            case STOP -> feederMotor.setPower(0.0);
        }
    }
}
```

### 3.4 `command/ShooterCommand.java`（全体）

```java
public class ShooterCommand {

    public static Command spinUp() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUp");
        // ★ setStop なし
    }

    public static Command spinUpReverse() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUpReverse");
    }

    public static Command stop() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::stop)
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("stopShooter");
    }

    public static Command retractFeeder() {
        ElapsedTime timer = new ElapsedTime();
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                    timer.reset();
                })
                .setIsDone(() -> timer.seconds() >= 0.2)
                .setInterruptible(true)
                .requires(FeederSubsystem.INSTANCE)
                .named("retractFeeder");
    }

    public static Command stopFeeder() {
        return new LambdaCommand()
                .setStart(() -> FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP))
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(FeederSubsystem.INSTANCE)
                .named("stopFeeder");
    }

    public static Command shootArtifacts(boolean isRetract) {
        SequentialGroup sequentialGroup;
        if (isRetract) {
            sequentialGroup = new SequentialGroup(
                    retractFeeder(),                        // (A) 0.2s 引き戻し
                    stopFeeder(),                           // (B) 即終了
                    spinUp(),                               // (C) isAtVelocity になるまで
                    new LambdaCommand()                     // (D) 状態セットして即終了
                            .setStart(() -> {
                                IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                                FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                            })
                            .setIsDone(() -> true)
                            .setInterruptible(true)
                            .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                            .named("feedAndIntake"));
        } else {
            sequentialGroup = new SequentialGroup(
                    spinUp(),
                    new LambdaCommand()
                            .setStart(() -> {
                                IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                                FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                            })
                            .setIsDone(() -> true)
                            .setInterruptible(true)
                            .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                            .named("feedAndIntake"));
        }
        return sequentialGroup
                .setInterruptible(true)
                .named("shootArtifacts");
    }

    public static Command reverseArtifacts() {
        return spinUpReverse();
    }

    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.stop();
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                    IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.STOP);
                })
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE)
                .named("stopAll");
    }
}
```

### 3.5 `opmode/teleop/Main.java`（バインディング部）

```java

@Override
public void onStartButtonPressed() {
    DriverControlledCommand driverControlled = new PedroDriverControlled(...);
    driverControlled.schedule();

    // ★ 問題のバインディング ★
    Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
            .whenBecomesTrue(ShooterCommand.shootArtifacts(true))   // ← 1回だけ生成
            .whenBecomesFalse(ShooterCommand.stopAll());            // ← 1回だけ生成

    Gamepads.gamepad1().y().and(Gamepads.gamepad1().x().not())
            .whenBecomesTrue(ShooterCommand.reverseArtifacts())
            .whenBecomesFalse(ShooterCommand.stopAll());

    Gamepads.gamepad1().a().and(Gamepads.gamepad1().b().not())
            .whenBecomesTrue(IntakeCommand.intake())
            .whenBecomesFalse(IntakeCommand.stopIntake());

    // ...
}
```

---

## 4. ユーザの期待動作 vs 実際の動作

### 期待

- X を押下 → フィーダー引き戻し → シューター回転up → フィーダー&インテーク回転 → 射出
- X を離す → 即時シューター/フィーダー/インテークが停止

### 実際（不具合）

- X を押下 → 期待通り射出開始
- X を離す → **モータ（特にフィーダー/インテーク）が止まらない**

---

## 5. 仮説と検証ポイント（図解してほしい論点）

問題は「shootArtifacts SequentialGroup の競合検出による中断」が完全には機能していないこと。具体的に怪しい点が複数あり、相互作用している可能性が高い。

### 仮説 A: **同一 Command インスタンスの使い回し問題**

`onStartButtonPressed` で `ShooterCommand.shootArtifacts(true)` と `ShooterCommand.stopAll()` を **1回だけ**
呼んでインスタンス生成し、Button のコールバックに登録している。X を押すたびに同じ SequentialGroup が再 schedule される。

理論上は `CommandGroup.stop()` で children deque がリセットされるはずだが、`retractFeeder()` 内の `ElapsedTime timer`
がクロージャでキャプチャされており、複数回の押下で予期せぬタイマー状態にならないか？

→ ただし `setStart` で `timer.reset()` しているので、原理上はリセットされる。

### 仮説 B: **`feedAndIntake` 子コマンドの即終了が中断 window を消している**

`shootArtifacts` の最後の子コマンド `feedAndIntake` は、

```java
.setStart(() ->{Intake=INTAKE;Feeder=FEED; })
        .

setIsDone(() ->true)
```

つまり「状態をセットして即完了」する。SequentialGroup はこの子の完了で全体完了 → 数フレーム後に runningCommands から消える。

その後ユーザが X を離して `stopAll` を schedule した時点では、**`shootArtifacts` はもう running ではない**ため、
`initCommand` の競合検出が何もマッチしない。`stopAll.start()` は走るので状態は STOP になる…はずだが、

**もし stopAll の schedule よりも先に shootArtifacts が完了して running から消えていれば、競合検出は起きない**。これは正常パスだが…

### 仮説 C: **CommandManager.run() の `update → schedule → cancel` の順序による race**

タイミングによっては、同一フレーム内で:

1. `shootArtifacts.update()` が走り、spinUp が `isAtVelocity` を達成 → `feedAndIntake.start()` が呼ばれて **状態
   FEED/INTAKE がセット**される
2. その同じフレームの後段で `scheduleCommands()` → `stopAll.start()` で **状態 STOP がセット**される
3. `cancelCommands()` で `shootArtifacts` が中断される

最終的な state は STOP のはずなので、これだけだと矛盾しない。次のフレームで `subsystem.periodic()` が STOP を読んでモータ停止する。

しかし **次のフレームに `shootArtifacts` が再 schedule される、あるいは何らかの再起がかかる**と、状態が再び FEED/INTAKE
に書き換えられる可能性がある。

### 仮説 D: **`spinUp` に `setStop` がない**

`spinUp` は中断されても target velocity をリセットしない。`stopAll.start()` 内の `ShooterSubsystem.INSTANCE.stop()`
に依存しているので、`stopAll` が実行されないと target velocity が残ったままになる。

仮説 A や C で stopAll の start が打ち消されるシナリオがあると、シューター速度が落ちないまま終わる。

### 仮説 E: **Bindings の Layer/エッジ判定**

`.and(...)` で生成された複合 Button が独立した Button インスタンスなので BindingManager に登録される（バイトコードで確認済み）。エッジ判定は
`previousValue` フィールドで行われており、ロジックは正しいように見える。

ただし、X と Y の `.and(.not())` バインディングが2つあり、Y の状態変化で X 側のコールバックが意図せず発火するパターンがあるかも？

---

## 6. NextFTC 内部仕様の確認結果（バイトコード+公式ソース）

| 確認項目                                | 結果                                                                  |
|-------------------------------------|---------------------------------------------------------------------|
| `Command.run()` の挙動                 | `schedule()` を呼ぶだけ                                                  |
| `Command` のデフォルト `interruptible`    | **`true`**                                                          |
| `CommandGroup` 構築時の requirements 集約 | あり（`setRequirements(union)` を呼ぶ）                                    |
| `CommandGroup.stop()` の挙動           | children deque をクリアして元の commands を再追加（リセット）                         |
| `SequentialGroup.update()` の挙動      | 現在の子を update → isDone なら removeFirst + 子を stop → 次の子の start を呼ぶ     |
| `CommandManager.run()` の順序          | **`update → schedule → cancel`**                                    |
| `initCommand` の競合解決                 | interruptible 競合相手はマーク、non-interruptible 相手があると **silently return** |
| Button.whenBecomesTrue/False        | Runnable を Callback に追加（複数登録可能）                                     |
| BindingsComponent                   | `preUpdate` で `BindingManager.update()` を呼ぶ                         |

---

## 7. 推奨修正案（4つ、組み合わせ可能）

### 修正 1: **バインディングをファクトリ化**（Command 使い回しを排除）

```java
.whenBecomesTrue(() ->ShooterCommand.

shootArtifacts(true).

schedule())
        .

whenBecomesFalse(() ->ShooterCommand.

stopAll().

schedule());
```

### 修正 2: **`feedAndIntake` を永続コマンド化** + `setStop` で自動 STOP

```java
new LambdaCommand()
    .

setStart(() ->{Intake=INTAKE;Feeder=FEED; })
        .

setIsDone(() ->false)                              // ★永続化
        .

setStop(interrupted ->{
        IntakeSubsystem.INSTANCE.

setState(IntakeState.STOP);
        FeederSubsystem.INSTANCE.

setState(FeederState.STOP);
    })
            .

requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
    .

named("feedAndIntake")
```

これにより、`shootArtifacts` SequentialGroup は X が押されている限り **永続的に running 状態にとどまる**。X 離下時の
`stopAll` schedule が必ず `findConflicts` でヒットして中断され、`setStop` が走ってクリーンに STOP 状態に戻る。

### 修正 3: **`spinUp` に `setStop` を追加**

```java
.setStop(interrupted ->{
        if(interrupted)ShooterSubsystem.INSTANCE.

stop();
})
```

### 修正 4: **`IntakeCommand.stopIntake()` に `requires` を追加**（別の関連バグ）

現在 requires が抜けていて競合検出が効かない。

---

## 8. 図解してほしい内容（リクエスト）

別の Claude にお願いしたいのは、以下を視覚的に整理した図:

1. **NextFTC の1ループ内の処理順序**（preUpdate → onUpdate → postUpdate 内で誰が何をするか、特に「BindingsComponent →
   CommandManager」の順序とタイミング）

2. **`shootArtifacts` SequentialGroup の状態遷移**（4つの子コマンドの遷移と、`feedAndIntake` の即完了によって全体が即終わる流れ）

3. **X 押下〜離下までのタイムライン**（押した瞬間/spinUp 中/feedAndIntake 完了直後/離した瞬間 の各タイミングで、state と
   running commands と実際のモータ出力がどうなるか、特にキャンセルが「効く」シナリオと「効かない」シナリオの比較）

4. **`CommandManager.initCommand` の競合検出フローチャート**（interruptible / non-interruptible / silently abort 分岐）

5. **`feedAndIntake` を永続化した修正後の挙動の比較**（修正前後でタイムラインがどう変わるか）

---

## 9. 補足: 関連ファイルパス

- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/command/ShooterCommand.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/command/IntakeCommand.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystem/ShooterSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystem/IntakeSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystem/FeederSubsystem.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/opmode/teleop/Main.java`
- `TeamCode/src/main/java/org/firstinspires/ftc/teamcode/config/Const.java`

NextFTC ドキュメント: <https://nextftc.dev>
NextFTC GitHub: <https://github.com/NextFTC/NextFTC>