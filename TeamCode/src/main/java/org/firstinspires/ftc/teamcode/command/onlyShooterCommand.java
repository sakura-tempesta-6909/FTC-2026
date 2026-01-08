package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class onlyShooterCommand {
    public static Command onlyShootArtifacts() {
        return new LambdaCommand()
                .setStart(() ->
                                ShooterSubsystem.INSTANCE.shoot().schedule()
                )
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE)
                .named("ShootArtifacts");
    }


    public static Command onlyReverseArtifacts () {
        return new LambdaCommand()
                .setStart(() ->
                                ShooterSubsystem.INSTANCE.reverse().schedule()
                )
                .setIsDone(() -> false)
                .requires(ShooterSubsystem.INSTANCE)
                .named("reverseArtifacts");
    }

    public static Command stopAll () {
        return new LambdaCommand()
                .setStart(() ->
                                ShooterSubsystem.INSTANCE.stop().schedule()
                ).setIsDone(() -> true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("stopAll");
    }
}

