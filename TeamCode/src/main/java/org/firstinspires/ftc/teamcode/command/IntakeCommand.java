package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;

/**
 * {@link IntakeSubsystem} のみを操作するコマンド集。
 * 全コマンドが永続 + setStop パターン。
 */
public class IntakeCommand {

    private IntakeCommand() {}

    /**
     * 取り込み方向にローラーを回し続ける。
     * <p>終了: 永続 (cancel のみ) / 中断時: Intake 停止 / requires: Intake
     */
    public static Command intake() {
        return new LambdaCommand()
                .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.INTAKE))
                .setIsDone(() -> false)
                .addRequirements(IntakeSubsystem.INSTANCE)
                .named("intake");
    }

    /**
     * 射撃中の補助用に、ゆっくり取り込み方向へ回し続ける。
     * 射撃の勢いでボールが外に飛び出すのを防ぐ目的。
     * <p>終了: 永続 (cancel のみ) / 中断時: Intake 停止 / requires: Intake
     */
    public static Command slowIntake() {
        return new LambdaCommand()
                .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.SLOW_INTAKE))
                .setIsDone(() -> false)
                .setStop(interrupted -> IntakeSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(IntakeSubsystem.INSTANCE)
                .named("slowIntake");
    }

    /**
     * 排出方向にローラーを逆転し続ける。
     * <p>終了: 永続 (cancel のみ) / 中断時: Intake 停止 / requires: Intake
     */
    public static Command outtake() {
        return new LambdaCommand()
                .setStart(() -> IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.REVERSE))
                .setIsDone(() -> false)
                .setStop(interrupted -> IntakeSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(IntakeSubsystem.INSTANCE)
                .named("outtake");
    }
}
