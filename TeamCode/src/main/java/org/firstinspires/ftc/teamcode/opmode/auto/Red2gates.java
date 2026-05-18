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
import org.firstinspires.ftc.teamcode.path.Blue2gatesPath;
import org.firstinspires.ftc.teamcode.path.Red2gatesPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Red 3gates")
@Configurable
public class Red2gates extends FTCBaseOpMode {
    private Red2gatesPath red2gatesPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(112.141, 135.624, Math.toRadians(0)));
        red2gatesPath = new Red2gatesPath(PedroComponent.follower());
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
                        new FollowPath(red2gatesPath.Path1, false, 1.0),
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
                        new FollowPath(red2gatesPath.Path2, false, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path3, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path4, false, 1.0),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(1.5),
                        new FollowPath(red2gatesPath.Path5,false,0.6)
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path6, false, 1.0),
                        ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path7, false, 1.0),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path8, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path9, false, 1.0),
                        IntakeCommand.intake()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new Delay(1.5),
                        new FollowPath(red2gatesPath.Path10,false,0.6)
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(red2gatesPath.Path11, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.slowIntake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(red2gatesPath.Path12, false, 1.0),
                new FollowPath(red2gatesPath.Path13, false, 1.0)
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.8;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                red2gatesPath.Path1, red2gatesPath.Path2, red2gatesPath.Path3,
                red2gatesPath.Path4, red2gatesPath.Path5, red2gatesPath.Path6,
                red2gatesPath.Path7, red2gatesPath.Path8);
    }
}
