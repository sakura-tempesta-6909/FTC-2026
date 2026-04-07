package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

/**
 * {@link ShooterSubsystem} だけを操作する単一責務コマンド集。
 * <p>
 * 「目標到達まで待つ」タイプ ({@link #spinUp()}, {@link #spinUpReverse()}) と、
 * 「目標到達後に維持し続ける」永続タイプ ({@link #holdRpm()}) に分かれる。
 * Routine ではこれらを SequentialGroup と ParallelGroup で組み合わせて使う。
 */
public class ShooterCommand {

    private ShooterCommand() {}

    /**
     * 直近の Limelight 距離から目標 RPM を決め、目標速度に到達するまで待つ。
     * 自然完了 (= 到達) では何もせず、外部から中断された場合のみ Shooter を停止する。
     * Routine 内では、この後 {@link #holdRpm()} を ParallelGroup で並べることで
     * 連射中もキャンセル安全な状態を保つ想定。
     */
    public static Command spinUp() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setStop(interrupted -> {
                    if (interrupted) ShooterSubsystem.INSTANCE.stop();
                })
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUp");
    }

    /**
     * 逆回転方向の目標速度をセットし、目標速度に到達するまで待つ。
     * 詰まり解除など。中断時のみ Shooter を停止する。
     */
    public static Command spinUpReverse() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setStop(interrupted -> {
                    if (interrupted) ShooterSubsystem.INSTANCE.stop();
                })
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUpReverse");
    }

    /**
     * Shooter の running owner として永続的に動く「維持コマンド」。
     * <p>
     * {@link #spinUp()} は目標到達後に自然完了して deque から消えるため、
     * その後の連射フェーズで Shooter を管理する Command が空白になる問題がある。
     * このコマンドを ParallelGroup の中で並列実行することで、Routine の
     * キャンセル時に必ず {@code setStop} が呼ばれて Shooter が停止する。
     * <p>
     * 自身では target velocity を変更しない (spinUp が設定した値をそのまま維持する)。
     */
    public static Command holdRpm() {
        return new LambdaCommand()
                .setIsDone(() -> false)
                .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("holdRpm");
    }
}
