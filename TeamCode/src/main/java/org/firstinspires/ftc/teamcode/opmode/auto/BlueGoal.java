package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.command.CorrectionCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.BlueGoalPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@Autonomous(name = "Blue Goal")
@Configurable
public class BlueGoal extends NextFTCOpMode {
    private BlueGoalPath blueGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private Telemetry driverStationTelemetry;

    public BlueGoal() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE, LimelightSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        driverStationTelemetry = telemetry;
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(20.494, 130.139, Math.toRadians(140)));
        blueGoalPath = new BlueGoalPath(PedroComponent.follower());
        Drawing.init();
        Drawing.drawDebug(PedroComponent.follower());
    }


    @Override
    public void onWaitForStart() {
        FTCBaseOpMode.showInitTelemetry(hardwareMap, driverStationTelemetry);
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(blueGoalPath.Path1, true, 0.3),
                CorrectionCommand.correct(), // Limelight で位置補正
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(blueGoalPath.Path2),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path3, false, 0.3),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(blueGoalPath.Path4),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootWithRetract()
                ),
                new FollowPath(blueGoalPath.Path5),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path6, false, 0.3),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(blueGoalPath.Path7),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootWithRetract()
                ),
                new FollowPath(blueGoalPath.Path8)
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        FTCBaseOpMode.updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower(), null,
                blueGoalPath.Path1, blueGoalPath.Path2, blueGoalPath.Path3,
                blueGoalPath.Path4, blueGoalPath.Path5, blueGoalPath.Path6,
                blueGoalPath.Path7, blueGoalPath.Path8);
    }
}
