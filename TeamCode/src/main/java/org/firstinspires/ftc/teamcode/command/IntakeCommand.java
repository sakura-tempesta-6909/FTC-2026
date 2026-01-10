package org.firstinspires.ftc.teamcode.command;

import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;

public class IntakeCommand {

    public static Command intake() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                        FeederSubsystem.INSTANCE.feed(),
                        IntakeSubsystem.INSTANCE.intake()
                ).schedule())
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("intakeArtifacts");
    }

    public static Command outtake() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                        FeederSubsystem.INSTANCE.retract(),
                        IntakeSubsystem.INSTANCE.retract()
                ).schedule())
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("retractArtifacts");
    }

    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                        FeederSubsystem.INSTANCE.stop(),
                        IntakeSubsystem.INSTANCE.stop()
                ).schedule())
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("stopAll");
    }
}
