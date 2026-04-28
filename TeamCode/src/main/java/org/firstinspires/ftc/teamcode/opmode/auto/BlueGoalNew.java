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
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.path.BlueGoalPathNew;

@Autonomous(name = "Blue Goal")
@Configurable
public class BlueGoalNew extends FTCBaseOpMode {
    private BlueGoalPathNew blueGoalPathnew;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(33.456, 135.469, Math.toRadians(180)));
        blueGoalPathnew = new BlueGoalPathNew(PedroComponent.follower());
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
                        new FollowPath(blueGoalPathnew.Path1, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path2, true, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path3, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path5, true, 1.0),
                        IntakeCommand.slowIntake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path6, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(blueGoalPathnew.Path7, false, 0.8),

                new ParallelDeadlineGroup(
                        new Delay(1.6),
                        new FollowPath(blueGoalPathnew.Path8,false,0.5)
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path9, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path10, false, 1.0),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path11, false, 0.7),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path12, true, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(blueGoalPathnew.Path13,false,1.0)

        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.6;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                blueGoalPathnew.Path1, blueGoalPathnew.Path2, blueGoalPathnew.Path3,
                blueGoalPathnew.Path4, blueGoalPathnew.Path5, blueGoalPathnew.Path6,
                blueGoalPathnew.Path7, blueGoalPathnew.Path8,blueGoalPathnew.Path9,
                blueGoalPathnew.Path10, blueGoalPathnew.Path11, blueGoalPathnew.Path12,
                blueGoalPathnew.Path13);
    }
}
