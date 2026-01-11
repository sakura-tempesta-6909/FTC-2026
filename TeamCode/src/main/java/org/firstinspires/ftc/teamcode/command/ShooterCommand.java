package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class ShooterCommand {
    public static Command shootArtifacts() {
        return new LambdaCommand()
                        .setStart(() -> {
                            ShooterSubsystem.INSTANCE.shoot().schedule();
                            if (ShooterSubsystem.INSTANCE.isAtVelocity()) {
                                ShooterSubsystem.INSTANCE.shoot().schedule();
                                FeederSubsystem.INSTANCE.feed().schedule();
                            } else {
                                ShooterSubsystem.INSTANCE.shoot().schedule();
                                FeederSubsystem.INSTANCE.stop().schedule();
                            }
                        })
                        .setStop(i -> FeederSubsystem.INSTANCE.stop().schedule())
                        .setIsDone(() -> false)
                        .requires(FeederSubsystem.INSTANCE)
                .named("shootArtifacts");
    }




    public static Command reverseArtifacts() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                ShooterSubsystem.INSTANCE.reverse(),
                                FeederSubsystem.INSTANCE.retract()
                        ).schedule()
                )
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("reverseArtifacts");
    }

    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> new ParallelGroup(
                                ShooterSubsystem.INSTANCE.stop(),
                                FeederSubsystem.INSTANCE.stop()
                        ).schedule()
                ).setIsDone(() -> true)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                .named("stopAll");
    }
}
