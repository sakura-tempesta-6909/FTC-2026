package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;

import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class ShooterCommand {
    public static Command shootArtifacts() {
        return new LambdaCommand()
                .setStart(() -> ShooterSubsystem.INSTANCE.setTargetRPM(Const.Shooter.Velocity.TARGET_RPM))
                .setUpdate(() -> {
                    if (ShooterSubsystem.INSTANCE.isAtVelocity()) {
                        FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                    } else {
                        FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                    }
                })
                .setStop(i -> FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP))
                        .setIsDone(() -> false)
                        .requires(ShooterSubsystem.INSTANCE,FeederSubsystem.INSTANCE)
                .named("shootArtifacts");
    }




    public static Command reverseArtifacts() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.setReverseTargetRPM(Const.Shooter.Velocity.REVERSE_TARGET_RPM);
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                })
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("reverseArtifacts");
    }

    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.stop().schedule();
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                })
                .setIsDone(() -> true)
                .named("stopAll");
    }
}
