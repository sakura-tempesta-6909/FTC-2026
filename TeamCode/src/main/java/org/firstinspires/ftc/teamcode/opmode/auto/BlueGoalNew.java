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
        PedroComponent.follower().setStartingPose(new Pose(32.288, 132.574, Math.toRadians(180)));
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
                        new FollowPath(blueGoalPathnew.Path1, true, 1.0),
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
                        new FollowPath(blueGoalPathnew.Path2, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(blueGoalPathnew.Path3, false, 0.5, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path4, true, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path5, false, 1.0),
                        IntakeCommand.slowIntake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        FollowPathWithTimeout.create(blueGoalPathnew.Path6, false, 0.5, PATH3_TIMEOUT_SECONDS),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(blueGoalPathnew.Path7, false, 1.0),

                new FollowPath(blueGoalPathnew.Path8, false, 1.0),

                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path9, true, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(blueGoalPathnew.Path10,false,1.0),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path11, false, 1.0),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path12, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueGoalPathnew.Path13, true, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                )

        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.7;
    private static final double PATH3_TIMEOUT_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                blueGoalPathnew.Path1, blueGoalPathnew.Path2, blueGoalPathnew.Path3,
                blueGoalPathnew.Path4, blueGoalPathnew.Path5, blueGoalPathnew.Path6,
                blueGoalPathnew.Path7, blueGoalPathnew.Path8);
    }
}
