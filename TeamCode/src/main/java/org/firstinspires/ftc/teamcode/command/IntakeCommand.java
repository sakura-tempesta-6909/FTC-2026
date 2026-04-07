package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;

/**
 * {@link IntakeSubsystem} だけを操作する単一責務コマンド集。
 * <p>
 * 全コマンドは「永続実行 + setStop で停止」パターン。中断時に必ず Intake が
 * STOP に戻るため、競合検出ベースの自動キャンセルだけで安全に止まる。
 */
public class IntakeCommand {

    private IntakeCommand() {}

    /** 取り込み方向にローラーを回し続ける。中断で停止。 */
    public static Command intake() {
        return new LambdaCommand()
                .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.INTAKE))
                .setIsDone(() -> false)
                .setStop(interrupted -> IntakeSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .requires(IntakeSubsystem.INSTANCE)
                .named("intake");
    }

    /** 排出方向にローラーを逆転し続ける。中断で停止。 */
    public static Command outtake() {
        return new LambdaCommand()
                .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.REVERSE))
                .setIsDone(() -> false)
                .setStop(interrupted -> IntakeSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .requires(IntakeSubsystem.INSTANCE)
                .named("outtake");
    }
}
