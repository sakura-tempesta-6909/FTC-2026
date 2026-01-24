package org.firstinspires.ftc.teamcode.command;

import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class ShooterCommand {

    /**
     * シューターを目標RPMまでスピンアップする
     * 完了条件: 目標回転数に達する
     */
    public static Command spinUp() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUp");
    }

    /**
     * シューターを逆回転でスピンアップする
     * 完了条件: 目標回転数に達する
     */
    public static Command spinUpReverse() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUpReverse");
    }

    /**
     * シューターを停止する
     * 完了条件: 即座に完了
     */
    public static Command stop() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::stop)
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("stopShooter");
    }

    /**
     * Feederを1秒間巻き取る
     * 完了条件: 1秒経過
     */
    public static Command retractFeeder() {
        ElapsedTime timer = new ElapsedTime();
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                    timer.reset();
                })
                .setIsDone(() -> timer.seconds() >= 1.0)
                .setInterruptible(true)
                .requires(FeederSubsystem.INSTANCE)
                .named("retractFeeder");
    }

    /**
     * 発射シーケンス全体
     * 1. Feederを1秒間巻き取る
     * 2. Shooterをスピンアップ（目標速度まで待機）
     * 3. FeederとIntakeを回転させる
     */
    public static Command shootArtifacts() {
        return new SequentialGroup(
                // 1. Feederを1秒間巻き取る
                retractFeeder(),

                // 2. Shooterをスピンアップ（目標速度に達するまで待機）
                spinUp(),

                // 3. FeederとIntakeを回転（継続コマンド）
                new LambdaCommand()
                        .setStart(() -> {
                            IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                            FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                        })
                        .setIsDone(() -> false)
                        .setInterruptible(true)
                        .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                        .named("feedAndIntake")
        ).named("shootArtifacts");
    }

    /**
     * 逆回転シーケンス
     */
    public static Command reverseArtifacts() {
        return spinUpReverse();
    }

    /**
     * 全停止
     */
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
