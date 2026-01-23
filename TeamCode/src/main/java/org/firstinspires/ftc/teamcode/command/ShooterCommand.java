package org.firstinspires.ftc.teamcode.command;

import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;

public class ShooterCommand {
    public static Command shootArtifacts() {
        ElapsedTime indexAlignTimer = new ElapsedTime();


        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.setState(
                            ShooterSubsystem.ShooterState.setTargetRPM
                    );
                    indexAlignTimer.reset();
                })
                .setUpdate(() -> {
                    if (indexAlignTimer.seconds() < Const.Feeder.Motor.isAttime) {
                        IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                        FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                        return;
                    }

                    if (ShooterSubsystem.INSTANCE.isAtVelocity()) {
                        IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                        FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                    } else {
                        IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.STOP);
                        FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                    }
                })
                .setStop(i ->
                        FeederSubsystem.INSTANCE.setState(
                                FeederSubsystem.FeederState.STOP
                        )
                )
                .setIsDone(() -> false)
                .requires(
                        ShooterSubsystem.INSTANCE,
                        FeederSubsystem.INSTANCE
                )
                .named("shootArtifacts");
    }


    public static Command reverseArtifacts() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.setState(ShooterSubsystem.ShooterState.setReverseTargetRPM);
                })
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("reverseArtifacts");
    }

    public static Command stopShooter() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.setState(ShooterSubsystem.ShooterState.stopShooter);
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                })
                .setIsDone(() -> true)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("stopAll");
    }
}
