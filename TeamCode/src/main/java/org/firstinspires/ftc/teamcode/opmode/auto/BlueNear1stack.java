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
import org.firstinspires.ftc.teamcode.path.BlueNear1stackPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Blue Near 1stack")
@Configurable
public class BlueNear1stack extends FTCBaseOpMode {
    private BlueNear1stackPath blueNear1stackPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(33.403, 135.541, Math.toRadians(180)));
        blueNear1stackPath = new BlueNear1stackPath(PedroComponent.follower());
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
                        new FollowPath(blueNear1stackPath.Path1, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueNear1stackPath.Path2, false, 1.0),
                        IntakeRoutine.intakeWithWeakFeed(),
                        ShooterCommand.holdRpm()
                ),
                // パス完了 or タイムアウトの早い方で終了
                new ParallelDeadlineGroup(
                        new FollowPath(blueNear1stackPath.Path3, false, 0.5),
                        IntakeRoutine.intakeWithHold()
                ),
                new ParallelDeadlineGroup(
                        new FollowPath(blueNear1stackPath.Path4, false, 1.0),
                        ShooterCommand.spinUpForPath(),
                        IntakeCommand.intake()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(blueNear1stackPath.Path5, false, 0.7),

                // パス完了 or タイムアウトの早い方で終了
                new FollowPath(blueNear1stackPath.Path6, false, 0.5),

                new FollowPath(blueNear1stackPath.Path7, false, 0.6)
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.8;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                blueNear1stackPath.Path1, blueNear1stackPath.Path2, blueNear1stackPath.Path3,
                blueNear1stackPath.Path4, blueNear1stackPath.Path5, blueNear1stackPath.Path6,
                blueNear1stackPath.Path7);
    }
}
