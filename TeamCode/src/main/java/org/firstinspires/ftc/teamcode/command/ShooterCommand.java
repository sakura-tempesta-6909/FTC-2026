package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

/**
 * {@link ShooterSubsystem} のみを操作するコマンド集。
 *
 * <ul>
 *   <li>条件完了型 ({@link #spinUp})
 *       — 目標速度に到達したら自然完了。中断時のみ Shooter を停止する。</li>
 *   <li>永続型 ({@link #spinUpReverse}, {@link #holdRpm})
 *       — 外部 cancel でのみ終了。停止は setStop に任せる。</li>
 * </ul>
 *
 * Routine 内では {@code spinUp → holdRpm} を SequentialGroup + ParallelGroup で
 * 繋ぐことで、「加速待ち → 連射維持」のフローを実現する。
 */
public class ShooterCommand {

    private ShooterCommand() {}

    /**
     * Limelight 距離から目標 RPM を自動決定し、到達するまで待つ。
     * <p>終了: isAtVelocity() == true / 中断時: Shooter 停止 / 自然完了時: 何もしない
     * <p>requires: Shooter
     * <p>自然完了後は Shooter のオーナーが空白になるため、
     * Routine では後段に {@link #holdRpm()} を置く必要がある。
     */
    public static Command spinUp() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setUpdate(ShooterSubsystem.INSTANCE::setTargetRPM) // 毎ループ距離から RPM を更新
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setStop(interrupted -> {
                    if (interrupted) ShooterSubsystem.INSTANCE.stop();
                })
                .setInterruptible(true)
                .addRequirements(ShooterSubsystem.INSTANCE)
                .named("spinUp");
    }

    /**
     * パスと並行して使うスピンアップ。中断されてもシューターを停止しない。
     * <p>ParallelDeadlineGroup(FollowPath, spinUpForPath()) のように使い、
     * パス完了後も RPM を維持したまま次のコマンド (shootContinuous 等) に繋げる。
     * <p>終了: isAtVelocity() == true / 中断時: 何もしない (RPM 維持) / requires: Shooter
     */
    public static Command spinUpForPath() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setUpdate(ShooterSubsystem.INSTANCE::setTargetRPM) // 毎ループ距離から RPM を更新
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setStop(interrupted -> { /* 中断時も停止しない: 次のコマンドで RPM を引き継ぐ */ })
                .setInterruptible(true)
                .addRequirements(ShooterSubsystem.INSTANCE)
                .named("spinUpForPath");
    }

    /**
     * 逆回転の目標速度をセットし、維持し続ける (詰まり解除等)。
     * <p>終了: 永続 (cancel のみ) / 中断時: Shooter 停止 / requires: Shooter
     */
    public static Command spinUpReverse() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
                .setIsDone(() -> false)
                .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(ShooterSubsystem.INSTANCE)
                .named("spinUpReverse");
    }

    /**
     * HOLD 状態を維持する。PID で微小な負 RPM を保ち、ボール接触等による
     * 前方向の自然回転を抑える。Intake 中などシューター停止相当の期間で使用する。
     * <p>終了: 永続 (cancel のみ) / 中断時: Shooter 停止 / requires: Shooter
     */
    public static Command hold() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::hold)
                .setIsDone(() -> false)
                .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(ShooterSubsystem.INSTANCE)
                .named("hold");
    }

    /**
     * spinUp 完了後に Shooter のオーナーとして居続ける維持コマンド。
     * target velocity は変更せず、spinUp が設定した値をそのまま維持する。
     * <p>終了: 永続 (cancel のみ) / 中断時: Shooter 停止 / requires: Shooter
     */
    public static Command holdRpm() {
        return new LambdaCommand()
                .setUpdate(ShooterSubsystem.INSTANCE::setTargetRPM) // 毎ループ距離から RPM を更新
                .setIsDone(() -> false)
                .setStop(interrupted -> ShooterSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(ShooterSubsystem.INSTANCE)
                .named("holdRpm");
    }
}
