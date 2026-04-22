package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.teamcode.command.FollowPathWithTimeout;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.BlueGoalPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Blue Goal")
@Configurable
public class BlueGoal extends FTCBaseOpMode {
    private BlueGoalPath blueGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(33.403, 135.541, Math.toRadians(180)));
        blueGoalPath = new BlueGoalPath(PedroComponent.follower());
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
                        new FollowPath(blueGoalPath.Path1, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path2, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(blueGoalPath.Path3, false, 0.5, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path5, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(blueGoalPath.Path6, false, 0.5, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path7, false, 1.0),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path8, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path9, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(blueGoalPath.Path10, false, 0.5, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                       new FollowPath(blueGoalPath.Path11, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPath.Path12, false, 1.0),
                        IntakeCommand.slowIntake()
                )
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.8;
    private static final double PATH3_TIMEOUT_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                blueGoalPath.Path1, blueGoalPath.Path2, blueGoalPath.Path3,
                blueGoalPath.Path4, blueGoalPath.Path5, blueGoalPath.Path6,
                blueGoalPath.Path7, blueGoalPath.Path8);
    }
}
