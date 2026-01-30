package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.path.RedGoalPath;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@Autonomous(name = "Red Goal")
@Configurable
public class RedGoal extends NextFTCOpMode {
    private RedGoalPath redGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    public RedGoal() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(117.386, 130.854, Math.toRadians(40)));
        redGoalPath = new RedGoalPath(PedroComponent.follower());
        ShooterCommand.stopAll();
        IntakeCommand.stopIntake();
        Drawing.init();
        Drawing.drawDebug(PedroComponent.follower());
    }


    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(redGoalPath.Path1, true, 0.3),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(redGoalPath.Path2),
                IntakeCommand.intake(),
                new FollowPath(redGoalPath.Path3, false, 0.3),
                IntakeCommand.stopIntake(),
                new FollowPath(redGoalPath.Path4),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(true),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(redGoalPath.Path5),
                IntakeCommand.intake(),
                new FollowPath(redGoalPath.Path6,false, 0.3),
                IntakeCommand.stopIntake(),
                new FollowPath(redGoalPath.Path7),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(true),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(redGoalPath.Path8)
        );
    }

    @Override
    public void onUpdate() {
        Drawing.drawDebug(PedroComponent.follower());
    }
}