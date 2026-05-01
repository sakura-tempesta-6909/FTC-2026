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
import org.firstinspires.ftc.teamcode.path.Blue3stacksPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.path.BlueGoalPath;

@Autonomous(name = "Blue 3stacks")
@Configurable
public class Blue3stacks extends FTCBaseOpMode {
    private Blue3stacksPath blue3stacksPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(33.456, 135.469, Math.toRadians(180)));
        blue3stacksPath = new Blue3stacksPath(PedroComponent.follower());
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
                        new FollowPath(blue3stacksPath.Path1, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path2, true, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path3, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path5, true, 1.0),
                        IntakeCommand.slowIntake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path6, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path7, false, 1.0),
                        IntakeCommand.intake()
                ),
                new FollowPath(blue3stacksPath.Path8,false,0.8),

                new ParallelDeadlineGroup(
                        new Delay(1.6),
                        new FollowPath(blue3stacksPath.Path9,false,0.5)
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path10, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path11, false, 1.0),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path12, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blue3stacksPath.Path13, true, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(blue3stacksPath.Path14,false,1.0)

        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.6;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                blue3stacksPath.Path1, blue3stacksPath.Path2, blue3stacksPath.Path3,
                blue3stacksPath.Path4, blue3stacksPath.Path5, blue3stacksPath.Path6,
                blue3stacksPath.Path7, blue3stacksPath.Path8, blue3stacksPath.Path9,
                blue3stacksPath.Path10, blue3stacksPath.Path11, blue3stacksPath.Path12,
                blue3stacksPath.Path13, blue3stacksPath.Path14);
    }
}
