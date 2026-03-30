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
import org.firstinspires.ftc.teamcode.path.BlueGoalPathFar;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@Autonomous(name = "Blue Goal")
@Configurable
public class RedGoalFar extends NextFTCOpMode {
    private BlueGoalPathFar blueGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    public RedGoalFar() {
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
        PedroComponent.follower().setStartingPose(new Pose(85.849, 9.210, Math.toRadians(90)));
        blueGoalPath = new BlueGoalPathFar(PedroComponent.follower());
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
                new FollowPath(blueGoalPath.Path1, true, 0.5),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(blueGoalPath.Path2,false,0.5),
                IntakeCommand.intake(),
                new FollowPath(blueGoalPath.Path3, false, 0.5),
                IntakeCommand.stopIntake(),
                new FollowPath(blueGoalPath.Path4,false,0.5),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                IntakeCommand.intake(),
                new FollowPath(blueGoalPath.Path5,false,0.5),
                IntakeCommand.stopIntake(),
                new FollowPath(blueGoalPath.Path6,false,0.5),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                IntakeCommand.intake(),
                new FollowPath(blueGoalPath.Path7,false,0.5),
                IntakeCommand.stopIntake(),
                new FollowPath(blueGoalPath.Path8,false,0.5),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(blueGoalPath.Path9,false,0.5)
        );
    }

    @Override
    public void onUpdate() {
        Drawing.drawDebug(PedroComponent.follower());
    }
}