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
import org.firstinspires.ftc.teamcode.path.RedGoalPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Red Goal")
@Configurable
public class RedGoal extends FTCBaseOpMode {
    private RedGoalPath redGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(119.138, 135.398, Math.toRadians(0)));
        redGoalPath = new RedGoalPath(PedroComponent.follower());
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
                        new FollowPath(redGoalPath.Path1, false, 1.0),
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
                        new FollowPath(redGoalPath.Path2, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(redGoalPath.Path3, false, 0.3, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path5, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(redGoalPath.Path6, false, 0.3, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path7, false, 1.0),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path8, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path9, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(redGoalPath.Path10, false, 0.3, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),

                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path11, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ) ,
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path12, false, 1.0),
                        IntakeCommand.slowIntake()
                )
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 2.0;
    private static final double PATH3_TIMEOUT_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                redGoalPath.Path1, redGoalPath.Path2, redGoalPath.Path3,
                redGoalPath.Path4, redGoalPath.Path5, redGoalPath.Path6,
                redGoalPath.Path7, redGoalPath.Path8);
    }
}
