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
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.RedGoalPathNew;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@Autonomous(name = "Red Goal New")
@Configurable
public class RedGoalNew extends NextFTCOpMode {
    private RedGoalPathNew redGoalPathNew;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private Telemetry driverStationTelemetry;

    public RedGoalNew() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        driverStationTelemetry = telemetry;
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(119.13834434860539, 135.39823008849552, Math.toRadians(00)));
        redGoalPathNew = new RedGoalPathNew(PedroComponent.follower());
        Drawing.init();
        Drawing.drawDebug(PedroComponent.follower());
    }


    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                // 後退しながらスピンアップ (移動時間でRPMを上げておく)
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path1, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path2, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                new ParallelDeadlineGroup(
                        new Delay(PATH3_TIMEOUT_SECONDS), // 壁に挟まっても次に進む
                        new FollowPath(redGoalPathNew.Path3, false, 0.8),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path5, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                new ParallelDeadlineGroup(
                        new Delay(PATH3_TIMEOUT_SECONDS), // 壁に挟まっても次に進む
                        new FollowPath(redGoalPathNew.Path6, false, 0.8),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(redGoalPathNew.Path7, false, 1.0),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path8, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                )
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.5;
    private static final double PATH3_TIMEOUT_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        FTCBaseOpMode.updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower(), null,
                redGoalPathNew.Path1, redGoalPathNew.Path2, redGoalPathNew.Path3,
                redGoalPathNew.Path4, redGoalPathNew.Path5, redGoalPathNew.Path6,
                redGoalPathNew.Path7, redGoalPathNew.Path8);
    }
}