# NextFTC コマンド & スケジューリング完全ガイド

このドキュメントは、NextFTC のコマンドベース設計を**正しく**使うためのリファレンスです。本プロジェクト (
`SAKURA_Tempesta/FTC-2026`) で見られたアンチパターンと、その正しい書き方を比較しながら解説します。

---

## 目次

1. [基本概念](#1-基本概念)
2. [Command のライフサイクル](#2-command-のライフサイクル)
3. [LambdaCommand の正しい使い方](#3-lambdacommand-の正しい使い方)
4. [requires と競合検出](#4-requires-と競合検出)
5. [interruptible とキャンセル](#5-interruptible-とキャンセル)
6. [Subsystem の設計パターン](#6-subsystem-の設計パターン)
7. [CommandGroup（順次/並列）の使い方](#7-commandgroup順次並列の使い方)
8. [スケジューリングの内部動作](#8-スケジューリングの内部動作)
9. [Bindings との連携](#9-bindings-との連携)
10. [本プロジェクトのアンチパターン集](#10-本プロジェクトのアンチパターン集)
11. [チェックリスト](#11-チェックリスト)

---

## 1. 基本概念

NextFTC のコマンドベース設計は、ロボット制御を以下の3層に分けます。

```
┌──────────────────────────────────────┐
│  OpMode (NextFTCOpMode)              │  ← ライフサイクル & コンポーネント登録
├──────────────────────────────────────┤
│  Command / CommandGroup              │  ← 「何をするか」の宣言
├──────────────────────────────────────┤
│  Subsystem                           │  ← ハードウェアの管理 & 状態保持
└──────────────────────────────────────┘
```

**鉄則**:

- **Subsystem** はハードウェアに直接触れる唯一の場所。状態を保持し、`periodic()` でモータ出力を反映する。
- **Command** は「Subsystem に命令を出す」だけ。直接モータには触らない。
- **OpMode** は Command を schedule して、Bindings を設定するだけ。ロジックを書かない。

---

## 2. Command のライフサイクル

```
                ┌──────────┐
       schedule │  start() │
       ────────►│ (1回呼ぶ)│
                └────┬─────┘
                     │
              ┌──────▼──────┐
              │  update()   │  ◄────┐
              │ (毎ループ)   │       │
              └──────┬──────┘       │
                     │              │
              ┌──────▼──────┐       │
              │  isDone()   │       │
              └──────┬──────┘       │
                false│              │
                     ├──────────────┘
                true │
              ┌──────▼─────────┐
              │ stop(false)    │  ← 自然完了
              └────────────────┘

   外部キャンセル時:
              ┌──────────────────┐
              │ stop(true)       │  ← 中断（interrupted=true）
              └──────────────────┘
```

### 各メソッドの責任

| メソッド                | 呼ばれるタイミング                      | 主な用途                                    |
|---------------------|--------------------------------|-----------------------------------------|
| `start()`           | schedule された直後、1回だけ            | 初期化、タイマーリセット、Subsystem の状態を「開始」にセット     |
| `update()`          | 毎ループ、`isDone()` が `true` を返すまで | 連続的な処理（**重い処理は禁止**）                     |
| `isDone()`          | 毎ループ、`update()` の後             | 完了判定。`true` を返すとそのコマンドは終わる              |
| `stop(interrupted)` | 完了 or 中断時、1回だけ                 | 後片付け。`interrupted == true` なら外部からのキャンセル |

### 重要な原則

- **`update()` は毎ループ呼ばれる** — `for` ループや `Thread.sleep` などは絶対禁止
- **`stop()` は必ず呼ばれる** — クリーンアップは必ず `stop()` に書く（例外: setStop を使わない場合は LambdaCommand
  では何もしないという挙動になる）
- **`isDone()` は副作用を持たせない** — 単純な状態チェックだけにする

---

## 3. LambdaCommand の正しい使い方

`LambdaCommand` は最も使うパターン。ビルダー形式で 1 つの Command を作る。

### 3.1 4つの基本パターン

#### パターン A: **「即時実行」コマンド**（瞬間的に何かする）

```java
public static Command setShooterRPM(double rpm) {
    return new LambdaCommand()
        .setStart(() -> ShooterSubsystem.INSTANCE.setTargetVelocity(rpm))
        .setIsDone(() -> true)                                  // 即完了
        .requires(ShooterSubsystem.INSTANCE)
        .named("setShooterRPM");
}
```

**用途**: 状態をセットして終わる。`InstantCommand` と等価。

#### パターン B: **「条件待ち」コマンド**（何かが完了するまで待つ）

```java
public static Command waitForShooterAtVelocity() {
    return new LambdaCommand()
        .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
        .requires(ShooterSubsystem.INSTANCE)
        .named("waitForVelocity");
}
```

**用途**: 「シューターが目標速度に達するまで待つ」など。`start` で何もせず、`isDone` だけ条件をチェック。

#### パターン C: **「タイマー」コマンド**（一定時間後に終わる）

```java
public static Command runFeederFor(double seconds) {
    ElapsedTime timer = new ElapsedTime();
    return new LambdaCommand()
        .setStart(() -> {
            FeederSubsystem.INSTANCE.setState(FeederState.FEED);
            timer.reset();                                       // ★必ずstartでresetする
        })
        .setIsDone(() -> timer.seconds() >= seconds)
        .setStop(interrupted -> FeederSubsystem.INSTANCE.setState(FeederState.STOP))
        .requires(FeederSubsystem.INSTANCE)
        .named("runFeederFor");
}
```

**ポイント**:

- `ElapsedTime` はクロージャでキャプチャする
- `setStart` で必ず `timer.reset()`（再 schedule 時に古い時刻が残らないように）
- `setStop` で必ず後片付け（中断されても自然完了でも STOP 状態に戻す）

#### パターン D: **「永続実行」コマンド**（外部から中断されるまで動く）

```java
public static Command holdFeedAndIntake() {
    return new LambdaCommand()
        .setStart(() -> {
            IntakeSubsystem.INSTANCE.setState(IntakeState.INTAKE);
            FeederSubsystem.INSTANCE.setState(FeederState.FEED);
        })
        .setIsDone(() -> false)                                  // ★永続化
        .setStop(interrupted -> {
            IntakeSubsystem.INSTANCE.setState(IntakeState.STOP);
            FeederSubsystem.INSTANCE.setState(FeederState.STOP);
        })
        .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
        .named("holdFeedAndIntake");
}
```

**用途**: 「ボタンを押している間だけ動かす」などのホールド系。

- `setIsDone(() -> false)` で永遠に終わらない
- 外部からの中断で `setStop` が走り、必ず STOP 状態に戻る
- これにより **「キャンセルが効く」コマンド** になる

> ⚠️ **これが本プロジェクトの shoot バグの正しい解法です。**

### 3.2 使ってよいメソッドと避けるべきメソッド

| メソッド                           | 役割     | 必須?                                  |
|--------------------------------|--------|--------------------------------------|
| `setStart(Runnable)`           | 開始時1回  | 状態セットがある場合は必須                        |
| `setUpdate(Runnable)`          | 毎ループ   | できれば使わない（Subsystem.periodic に書く方が良い） |
| `setIsDone(Supplier<Boolean>)` | 完了判定   | 必須（書かないと永遠に終わらない）                    |
| `setStop(Consumer<Boolean>)`   | 終了時1回  | **副作用を起こすコマンドは必須**                   |
| `requires(Object...)`          | リソース宣言 | **必須**                               |
| `setInterruptible(boolean)`    | 中断可否   | デフォルト true。明示的に false にする時だけ         |
| `named(String)`                | デバッグ名  | あると debug が楽                         |

### 3.3 やってはいけないこと

❌ **`setUpdate` に時間のかかる処理を書く**

```java
.setUpdate(() -> {
    for (int i = 0; i < 1000; i++) doSomething();   // ループは禁止
})
```

❌ **`setStart` を「使い回せない」前提で書く**

```java
ElapsedTime timer = new ElapsedTime();   // クロージャで保持
return new LambdaCommand()
    .setStart(() -> { /* timer.reset() がない */ })   // ❌ 2回目で動かない
    .setIsDone(() -> timer.seconds() >= 1.0);
```

❌ **`setStop` で後片付けを忘れる**

```java
.setStart(() -> motor.setPower(1.0))
.setIsDone(() -> someCondition)
// ❌ 中断時にモータが回りっぱなし
```

---

## 4. requires と競合検出

### 4.1 `requires` の役割

`requires(...)` で「このコマンドは X というリソースを使う」と宣言します。これにより `CommandManager` は *
*同じリソースを使う別のコマンドが同時に走らないように**自動的に制御します。

```java
.requires(ShooterSubsystem.INSTANCE)              // シューターを占有
.requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)   // 複数可
```

### 4.2 競合検出のルール

**新しいコマンド N がスケジュールされた時**、CommandManager は:

1. 既に動いている全コマンドを調べる
2. それぞれの `requirements` と N の `requirements` の **積集合** を計算
3. 積集合が空でない（＝競合する）コマンドが見つかった場合:
    - 既存コマンドが `interruptible == true` → **既存をキャンセルして N を起動**
    - 既存コマンドが `interruptible == false` → **N を silently drop（捨てる）**

### 4.3 競合検出を効かせるための鉄則

✅ **副作用のあるコマンドは必ず `requires(...)` する**

```java
public static Command stopAll() {
    return new LambdaCommand()
        .setStart(() -> { /* shooter, feeder, intake を STOP */ })
        .setIsDone(() -> true)
        .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE)
        .named("stopAll");
}
```

❌ **requires が抜けたコマンド**

```java
public static Command stopIntake() {
    return new LambdaCommand()
        .setStart(...)
        .setIsDone(() -> true)
        // ★ requires がない → 競合検出が効かない → 何も止まらない
        .named("stopIntake");
}
```

> 本プロジェクトの `IntakeCommand.stopIntake()` がまさにこのバグ。

### 4.4 CommandGroup の自動集約

`SequentialGroup(a, b, c)` のような CommandGroup は、コンストラクタで **子の `requirements` を自動集約** します。

```java
SequentialGroup group = new SequentialGroup(
    cmdA,   // requires Feeder
    cmdB,   // requires Shooter
    cmdC    // requires Intake
);
// group.requirements == { Feeder, Shooter, Intake } が自動セットされる
```

つまり **グループ自体に `requires(...)` を呼ぶ必要はない**。集約は子の構成時点で確定するので、**子の `requires` は必ず正しく書くこと
**。

---

## 5. interruptible とキャンセル

### 5.1 デフォルトは `true`

すべての Command は **デフォルトで `interruptible == true`**。明示的に `setInterruptible(false)` した時だけ非中断可能になります。

### 5.2 `setInterruptible(false)` を使うべき場面

ほとんどの場合は **デフォルト（true）のままで良い**。`false` にすべきは、以下のような「途中で止めると物理的に危険」な処理だけ:

- アームを安全位置まで持ち上げる初期化シーケンス
- センサキャリブレーション

それ以外で `false` にすると、競合した時に**新しいコマンドが silently drop** されて非常に分かりにくいバグになります。

### 5.3 「キャンセルが効く」コマンドの設計原則

> **副作用を起こす（モータを回す等）コマンドは、必ず `setStop` で副作用を取り消せるように書く。**

例: 「シューターを回して、ボタンを離したら止める」

✅ **正しい設計**

```java
public static Command shootWhileHeld() {
    return new LambdaCommand()
        .setStart(() -> {
            ShooterSubsystem.INSTANCE.setTargetRPM();
            IntakeSubsystem.INSTANCE.setState(IntakeState.INTAKE);
            FeederSubsystem.INSTANCE.setState(FeederState.FEED);
        })
        .setIsDone(() -> false)                       // 永続実行
        .setStop(interrupted -> {                     // ★中断時に必ず STOP
            ShooterSubsystem.INSTANCE.stop();
            IntakeSubsystem.INSTANCE.setState(IntakeState.STOP);
            FeederSubsystem.INSTANCE.setState(FeederState.STOP);
        })
        .requires(ShooterSubsystem.INSTANCE, IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
        .named("shootWhileHeld");
}
```

そして Main.java で:

```java
Gamepads.gamepad1().x()
    .whenBecomesTrue(() -> ShooterCommand.shootWhileHeld().schedule());
// ↑ whenBecomesFalse は不要！
//   別のコマンド（例えば違うボタンの spinUp や stopAll）を schedule すると
//   競合検出で自動的にこのコマンドが中断 → setStop が走って自動停止
```

❌ **アンチパターン**: 「外部の stopAll コマンドに依存して止める」設計

- shootArtifacts と stopAll が別々で、どちらが先に動くかタイミングに依存する
- shootArtifacts 内部の状態セットを stopAll が後から打ち消すという**時系列依存**
- これが本プロジェクトの不具合の本質的な原因

---

## 6. Subsystem の設計パターン

### 6.1 シングルトンパターン

```java
public class IntakeSubsystem implements Subsystem {
    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();
    private IntakeSubsystem() {}   // ← private 化が望ましい
    // ...
}
```

`SubsystemComponent(IntakeSubsystem.INSTANCE)` で OpMode に登録すると、`initialize()` と `periodic()` が自動的に呼ばれます。

### 6.2 状態機械（State Machine）パターン

Subsystem に enum 状態を持たせ、`periodic()` で switch する形式。

```java
public enum IntakeState { INTAKE, STOP, REVERSE }
private IntakeState state = IntakeState.STOP;

public void setState(IntakeState s) { this.state = s; }

@Override public void periodic() {
    switch (state) {
        case INTAKE  -> intakeMotor.setPower(1.0);
        case REVERSE -> intakeMotor.setPower(-1.0);
        case STOP    -> intakeMotor.setPower(0.0);
    }
}
```

✅ **メリット**:

- 命令が冪等（何度同じ状態をセットしても安全）
- Command 側は `setState(...)` を呼ぶだけでシンプル
- デバッグが楽（state 変数を見れば現在何をしているか分かる）

⚠️ **注意点**:

- **Command の `setStop` で必ず安全な状態に戻すこと**
- そうしないと「Command は終わったがモータは回りっぱなし」というバグになる

### 6.3 直接命令パターン（非推奨）

```java
public void runIntake() { intakeMotor.setPower(1.0); }
public void stopIntake() { intakeMotor.setPower(0.0); }
```

これでも動きますが、状態が「コマンドの実行履歴」に分散してしまうのでデバッグしにくい。状態機械パターンの方が良いです。

---

## 7. CommandGroup（順次/並列）の使い方

### 7.1 4種類のグループ

| グループ                                         | 終了条件            | 使い所                             |
|----------------------------------------------|-----------------|---------------------------------|
| `SequentialGroup(a, b, c)`                   | 全部完了            | 「A → B → C」と順次実行                |
| `ParallelGroup(a, b)`                        | **全部完了**        | 「A と B を同時に始めて両方完了を待つ」          |
| `ParallelRaceGroup(a, b)`                    | **どれか1つ完了**     | 「A か B のどちらか早い方で終わる」            |
| `ParallelDeadlineGroup(deadline, others...)` | **deadline 完了** | 「deadline が終わったら others も全部止める」 |

### 7.2 タイムアウトを書く正しい方法

❌ **アンチパターン**: `ParallelGroup` でタイムアウトを意図する

```java
new ParallelGroup(
    ShooterCommand.shootArtifacts(false),
    new Delay(3)
)
// ParallelGroup は「全部完了で終了」なので、
// shootArtifacts が早く終わっても Delay の3秒は待つ
// shootArtifacts が長引いても Delay は止めない（意味がない）
```

✅ **正しい書き方 1**: `ParallelDeadlineGroup`

```java
new ParallelDeadlineGroup(
    new Delay(3),                              // ★これがdeadline
    ShooterCommand.shootArtifacts(false)       // 3秒後に強制終了される
)
```

✅ **正しい書き方 2**: `endAfter` ヘルパー

```java
ShooterCommand.shootArtifacts(false).endAfter(3.0)
// 内部的に ParallelRaceGroup(cmd, Delay(3)) と等価
```

### 7.3 SequentialGroup の使いどころ

```java
new SequentialGroup(
    ArmCommand.lift(),                  // 1. アーム上げる
    new Delay(0.2),                     // 2. 0.2秒待つ
    ClawCommand.open(),                 // 3. クローを開く
    ArmCommand.lower()                  // 4. アーム下げる
)
```

`then()` ヘルパーでも書けます:

```java
ArmCommand.lift()
    .then(new Delay(0.2))
    .then(ClawCommand.open())
    .then(ArmCommand.lower())
```

### 7.4 Group の `requirements` は自動集約される

```java
SequentialGroup group = new SequentialGroup(
    cmdA,    // requires Feeder
    cmdB,    // requires Shooter
);
// group.requirements == {Feeder, Shooter}
// 別途 group.requires(...) を呼ぶ必要はない
```

---

## 8. スケジューリングの内部動作

### 8.1 1ループの全体像

NextFTCOpMode の1ループは以下の3段階:

```
┌─ preUpdate（登録順）───────────────────────────────┐
│  1. SubsystemComponent.preUpdate                  │
│       → 全 Subsystem.periodic() を実行             │  ← この時点でモータ出力反映
│  2. BulkReadComponent.preUpdate                   │
│  3. BindingsComponent.preUpdate                   │
│       → 全 Button のエッジ判定                      │
│       → 該当 Runnable.run() を実行                 │  ← schedule() が呼ばれるかも
└────────────────────────────────────────────────────┘
                       ↓
┌─ user.onUpdate() ─────────────────────────────────┐
│  ユーザ定義の毎ループ処理（テレメトリ表示など）        │
└────────────────────────────────────────────────────┘
                       ↓
┌─ postUpdate（登録順の逆）─────────────────────────┐
│  ...                                              │
│  CommandManager.postUpdate                        │
│    → CommandManager.run() を実行                  │
│        ① running の update() + isDone判定          │
│        ② scheduleCommands() (initCommand)        │
│        ③ cancelCommands()                        │
└────────────────────────────────────────────────────┘
```

### 8.2 `CommandManager.run()` の内部

```kotlin
fun run() {
    // ① 全実行中コマンドを update
    for (command in runningCommands) {
        command.update()
        if (command.isDone) {
            commandsToCancel += Pair(command, false)   // 自然完了マーク
        }
    }
    
    // ② 新しくスケジュールされたコマンドを処理
    scheduleCommands()    // → initCommand を呼ぶ
    
    // ③ キャンセル対象を実際に止める
    cancelCommands()      // → cancel() を呼ぶ
}
```

**順序が重要**: `update → schedule → cancel`

これは「1ループ内で先に走っている処理を進めてから、新しい命令を取り込み、最後に古い命令を片付ける」という流れ。

### 8.3 `initCommand` の競合検出フロー

```kotlin
private fun initCommand(command: Command) {
    val requirements = expandRequirements(command.requirements)
    
    for (otherCommand in runningCommands) {
        val otherRequirements = expandRequirements(otherCommand.requirements)
        
        for (requirement in requirements) {
            if (otherRequirements.contains(requirement)) {
                // 競合発見
                if (otherCommand.interruptible) {
                    commandsToCancel += Pair(otherCommand, true)   // 既存を中断マーク
                } else {
                    return    // ★ 新コマンドを silently drop
                }
            }
        }
    }
    
    command.start()                  // 新コマンドの start を実行
    runningCommands += command
}
```

### 8.4 「同じ Command インスタンスを使い回す」ことについて

**結論: 動くが脆い。新規生成を推奨。**

理論的には:

- `Command.run()` は `schedule()` を呼ぶだけ
- `CommandGroup.stop()` は children deque をリセットする
- だから同じインスタンスを再 schedule しても動作するはず

ただし以下の理由で **毎回新規生成**の方が安全:

1. **クロージャでキャプチャした状態が予期せず残る**（`ElapsedTime` とか）
2. **インスタンスのライフサイクルを管理しなくて良い**ので考えることが減る
3. **読み手にとっても意図が明確**

### 8.5 推奨スケジューリング方法

| 方法                                                 | 用途                                     |
|----------------------------------------------------|----------------------------------------|
| `command.schedule()`                               | onStartButtonPressed や onUpdate から直接呼ぶ |
| `CommandManager.INSTANCE.scheduleCommand(command)` | 同上（古い書き方）                              |
| `() -> command.schedule()`                         | Bindings から呼ぶ時のラッパー                    |

**Bindings から渡す時は必ずラッパーを使う**:

✅ **正しい**

```java
.whenBecomesTrue(() -> ShooterCommand.shootWhileHeld().schedule())
```

❌ **アンチパターン**

```java
.whenBecomesTrue(ShooterCommand.shootWhileHeld())   // インスタンス使い回し
```

理由: `Command implements Runnable` なので動きはするが、上述の通り脆い。明示的なファクトリ Runnable にすれば毎回新規生成される。

---

## 9. Bindings との連携

### 9.1 基本パターン

```java
Gamepads.gamepad1().a()
    .whenBecomesTrue(() -> SomeCommand.doSomething().schedule())
    .whenBecomesFalse(() -> SomeCommand.stop().schedule());
```

### 9.2 「ホールド系」操作

「ボタンを押している間だけ動かす」パターン。これは2つの書き方があります:

#### 書き方 A: 永続コマンド + 競合検出に任せる（**推奨**）

```java
// ShooterCommand.java
public static Command shootWhileHeld() {
    return new LambdaCommand()
        .setStart(() -> { /* 状態をONに */ })
        .setIsDone(() -> false)                          // 永続
        .setStop(i -> { /* 状態をOFFに */ })             // 中断時に自動停止
        .requires(...)
        .named("shootWhileHeld");
}

// Main.java
Gamepads.gamepad1().x()
    .whenBecomesTrue(() -> ShooterCommand.shootWhileHeld().schedule());

// 別のコマンドを schedule すると、競合検出で自動的に shootWhileHeld が中断される
// 例: 別ボタン押下、または明示的に stop コマンドを schedule する
```

#### 書き方 B: rising/falling 両方で別コマンドを schedule

```java
Gamepads.gamepad1().x()
    .whenBecomesTrue(() -> ShooterCommand.startShooting().schedule())
    .whenBecomesFalse(() -> ShooterCommand.stopShooting().schedule());
```

これでも動きますが、**書き方 A の方が「ホールド」の意図が明確**で、しかも「離す前に他のコマンドが割り込んだ場合」も自動的に正しく動作します。

### 9.3 修飾キー的なパターン

「X だけ押されたら shoot、X+Y なら別の動作」のような場合は本プロジェクトのように `.and(.not())` で書きます:

```java
Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
    .whenBecomesTrue(() -> ShooterCommand.shoot().schedule());

Gamepads.gamepad1().x().and(Gamepads.gamepad1().y())
    .whenBecomesTrue(() -> ShooterCommand.shootSpecial().schedule());
```

---

## 10. 本プロジェクトのアンチパターン集

### 10.1 ❌ Command インスタンスの使い回し

```java
// Main.java
.whenBecomesTrue(ShooterCommand.shootArtifacts(true))    // 1個生成して使い回し
.whenBecomesFalse(ShooterCommand.stopAll());
```

#### ✅ 修正

```java
.whenBecomesTrue(() -> ShooterCommand.shootArtifacts(true).schedule())
.whenBecomesFalse(() -> ShooterCommand.stopAll().schedule());
```

---

### 10.2 ❌ `requires` 漏れ

```java
// IntakeCommand.java
public static Command stopIntake() {
    return new LambdaCommand()
        .setStart(...)
        .setIsDone(() -> true)
        .named("stopAll");
        // ★ requires が抜けている
}
```

#### ✅ 修正

```java
public static Command stopIntake() {
    return new LambdaCommand()
        .setStart(...)
        .setIsDone(() -> true)
        .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
        .named("stopIntake");
}
```

---

### 10.3 ❌ `setStop` がない副作用コマンド

```java
public static Command spinUp() {
    return new LambdaCommand()
        .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
        .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
        .requires(ShooterSubsystem.INSTANCE)
        .named("spinUp");
        // ★ 中断されてもターゲット velocity がリセットされない
}
```

#### ✅ 修正

```java
public static Command spinUp() {
    return new LambdaCommand()
        .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
        .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
        .setStop(interrupted -> {
            if (interrupted) ShooterSubsystem.INSTANCE.stop();
        })
        .requires(ShooterSubsystem.INSTANCE)
        .named("spinUp");
}
```

---

### 10.4 ❌ 「即終了する setStart」で状態をセットして放置

```java
new LambdaCommand()
    .setStart(() -> {
        IntakeSubsystem.INSTANCE.setState(IntakeState.INTAKE);
        FeederSubsystem.INSTANCE.setState(FeederState.FEED);
    })
    .setIsDone(() -> true)                  // ★ 1フレームで完了
    .requires(...)
    .named("feedAndIntake")
```

これは「状態をセットして即終わる」設計。Command は終わるが Subsystem の状態はセットされたまま残る。**外部の別コマンドに「止めて」もらう必要がある
**ため、競合検出のタイミングに依存して脆くなる。

#### ✅ 修正: 永続コマンドに変える

```java
new LambdaCommand()
    .setStart(() -> {
        IntakeSubsystem.INSTANCE.setState(IntakeState.INTAKE);
        FeederSubsystem.INSTANCE.setState(FeederState.FEED);
    })
    .setIsDone(() -> false)                 // ★ 永続実行
    .setStop(interrupted -> {
        IntakeSubsystem.INSTANCE.setState(IntakeState.STOP);
        FeederSubsystem.INSTANCE.setState(FeederState.STOP);
    })
    .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
    .named("feedAndIntake")
```

これにより「中断されたら必ず STOP に戻る」が保証される。

---

### 10.5 ❌ `ParallelGroup` でタイムアウトを意図する

```java
// RedGoal.java（auto）
new ParallelGroup(
    ShooterCommand.shootArtifacts(false),
    new Delay(3)
)
```

ParallelGroup は「全部完了で終わる」ので、タイムアウトの意味がない。

#### ✅ 修正

```java
ShooterCommand.shootArtifacts(false).endAfter(3.0)
```

または:

```java
new ParallelDeadlineGroup(
    new Delay(3),                              // deadline
    ShooterCommand.shootArtifacts(false)
)
```

---

## 11. チェックリスト

新しい Command を書く時、以下を確認してください:

- [ ] **`requires(...)` を書いたか？** 副作用がある（モータを回す等）なら必須
- [ ] **`setStop` で副作用を取り消しているか？** 中断時に物理的に安全な状態に戻るか
- [ ] **`isDone` の戻り値は正しいか？** 即完了なら `true`、永続なら `false`、条件待ちなら適切な条件
- [ ] **`setStart` のクロージャが再呼び出しに耐えるか？** `ElapsedTime` は必ず `reset()` する
- [ ] **`update` に重い処理を書いていないか？**
- [ ] **コマンド名を `named(...)` で付けたか？** デバッグで助かる

新しい Bindings を書く時:

- [ ] **コマンドをラムダでラップしたか？** `() -> cmd.schedule()` の形式
- [ ] **「ホールド系」なら永続コマンド + setStop の設計を使っているか？**
- [ ] **`whenBecomesFalse` で「打ち消し」コマンドを書く前に、永続コマンド方式を検討したか？**

新しい Subsystem を書く時:

- [ ] **シングルトン (`INSTANCE`) になっているか？**
- [ ] **`initialize()` でハードウェア初期化と状態リセットをしているか？**
- [ ] **`periodic()` が冪等か？**（同じ state を何度読んでも同じ動作）

---

## 12. 参考リンク

- 公式ドキュメント: <https://nextftc.dev>
- GitHub: <https://github.com/NextFTC/NextFTC>
- 主要ガイド:
    - Commands: <https://nextftc.dev/nextftc/concepts/commands>
    - Subsystems: <https://nextftc.dev/nextftc/concepts/subsystems>
    - OpModes: <https://nextftc.dev/nextftc/concepts/opmodes>
    - Command Groups: <https://nextftc.dev/nextftc/commands/groups>
    - Custom Commands: <https://nextftc.dev/nextftc/commands/custom-commands>
    - Bindings: <https://nextftc.dev/bindings/buttons>

---

## 付録: 「Command の正解パターン」早見表

### A. 即時実行 (InstantCommand 相当)

```java
new LambdaCommand()
    .setStart(() -> /* 1回だけの処理 */)
    .setIsDone(() -> true)
    .requires(...)
```

### B. 条件待ち (WaitUntil 相当)

```java
new LambdaCommand()
    .setIsDone(() -> /* 条件 */)
```

### C. 時間待ち (Delay 相当)

```java
new Delay(seconds)   // ← 専用クラスがあるのでこちらを使う
```

### D. 一定時間動かして自動停止

```java
ElapsedTime timer = new ElapsedTime();
new LambdaCommand()
    .setStart(() -> { /* 開始 */ ; timer.reset(); })
    .setIsDone(() -> timer.seconds() >= duration)
    .setStop(i -> /* 停止 */)
    .requires(...)
```

### E. 永続実行（外部から中断されるまで）

```java
new LambdaCommand()
    .setStart(() -> /* 開始 */)
    .setIsDone(() -> false)
    .setStop(i -> /* 停止 */)
    .requires(...)
```

### F. 順次実行

```java
new SequentialGroup(cmdA, cmdB, cmdC)
// または
cmdA.then(cmdB).then(cmdC)
```

### G. 並列実行（全部待つ）

```java
new ParallelGroup(cmdA, cmdB)
// または
cmdA.and(cmdB)
```

### H. タイムアウト付き実行

```java
cmd.endAfter(seconds)
// または
new ParallelDeadlineGroup(new Delay(seconds), cmd)
```

### I. 競合させて自動切替

```java
// ボタン1: shootA を schedule
// ボタン2: shootB を schedule（同じリソースを使う）
// → ボタン2押下時に shootA が自動的に setStop で止まる
```