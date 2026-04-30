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
    private RedGoalPath redGoalPathNew;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(112.230, 135.047, Math.toRadians(0)));
        redGoalPathNew = new RedGoalPath(PedroComponent.follower());
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
                        new FollowPath(redGoalPathNew.Path2, true, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path3, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path5, true, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path6, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path7, false, 0.8),
                        IntakeCommand.intake()
                ),
                new FollowPath(redGoalPathNew.Path8,false,0.8),
                new ParallelDeadlineGroup(
                        new Delay(1.6),
                        new FollowPath(redGoalPathNew.Path9,false,0.5)
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path10, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path11, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path12, false, 0.7),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPathNew.Path13, true, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(redGoalPathNew.Path14,false,1.0)

        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.6;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                redGoalPathNew.Path1, redGoalPathNew.Path2, redGoalPathNew.Path3,
                redGoalPathNew.Path4, redGoalPathNew.Path5, redGoalPathNew.Path6,
                redGoalPathNew.Path7, redGoalPathNew.Path8);
    }
}
