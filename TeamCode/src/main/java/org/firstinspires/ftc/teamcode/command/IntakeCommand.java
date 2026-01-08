package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;

import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;


public class IntakeCommand {
    public static Command intakeArtifacts() {
        return new LambdaCommand()
                .setStart(() ->
                                IntakeSubsystem.INSTANCE.intake().schedule())
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE)
                .named("intakeArtifacts");

    }

    public static Command intakeReverseArtifacts() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                IntakeSubsystem.INSTANCE.outtake(),
                                FeederSubsystem.INSTANCE.stop()

                        ).schedule()
                )
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("intakeReverseArtifacts");

    }

    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                IntakeSubsystem.INSTANCE.stop(),
                                FeederSubsystem.INSTANCE.stop()
                        ).schedule()
                ).setIsDone(() -> true)
                .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("stopAll");
    }



}
