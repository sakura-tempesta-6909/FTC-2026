package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class FeederCommand {
    public static Command feedArtifacts() {
        return new LambdaCommand()
                .setStart(() ->
                        FeederSubsystem.INSTANCE.feed().schedule())
                .setIsDone(() -> false)
                .requires(FeederSubsystem.INSTANCE)
                .named("feedArtifacts");
    }


    public static Command feedReverseArtifacts () {
        return new LambdaCommand()
                .setStart(() ->
                                FeederSubsystem.INSTANCE.retract().schedule())
                .setIsDone(() -> false)
                .requires(FeederSubsystem.INSTANCE)
                .named("feedReverseArtifacts");
    }

    public static Command stopAll () {
        return new LambdaCommand()
                .setStart(() ->
                                FeederSubsystem.INSTANCE.stop().schedule())
                .setIsDone(() -> true)
                .requires(FeederSubsystem.INSTANCE)
                .named("stopAll");
    }
}

