package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;

public class IntakeCommand {


    public static Command intakeArtifacts() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                IntakeSubsystem.INSTANCE.intake(),
                                FeederSubsystem.INSTANCE.feed()
                        ).schedule()
                )
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("intakeArtifacts");
    }

    public static Command outtakeArtifacts() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                IntakeSubsystem.INSTANCE.outtake(),
                                FeederSubsystem.INSTANCE.retract()
                        ).schedule()
                )
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("outtakeArtifacts");
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

