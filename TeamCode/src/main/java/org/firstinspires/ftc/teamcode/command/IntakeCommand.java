package org.firstinspires.ftc.teamcode.command;

import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;

public class IntakeCommand {

    public static Command intake() {
        return new LambdaCommand()
                .setStart(() -> {
                    IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.WEAKFEED);
                })
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE)
                .named("intakeArtifacts");
    }

    public static Command outtake() {
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                    IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.REVERSE);
                })
                .setIsDone(() -> false)
                .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("retractArtifacts");
    }

    public static Command stopIntake() {
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                    IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.STOP);
                })
                .setIsDone(() -> true)
                .named("stopAll");
    }
}
