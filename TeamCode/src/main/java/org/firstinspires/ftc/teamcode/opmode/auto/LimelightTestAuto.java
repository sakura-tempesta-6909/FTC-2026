package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.teamcode.command.CorrectionCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;

/**
 * Limelight 位置補正のテスト用 Auto。
 * 後退 → 補正 → 射撃 → 初期位置に戻る。
 * 戻った位置が初期位置と一致するかで補正精度を確認する。
 */
@Autonomous(name = "Limelight Test Auto")
@Configurable
public class LimelightTestAuto extends FTCBaseOpMode {

    private static final Pose START_POSE = new Pose(118.889, 122.465, Math.toRadians(40));
    private static final Pose BACK_POSE = new Pose(95.689, 86.043);
    private static final double SHOOT_DURATION_SECONDS = 2.0;

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private PathChain pathBack;
    private PathChain pathReturn;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(START_POSE);

        pathBack = PedroComponent.follower().pathBuilder()
                .addPath(new BezierLine(START_POSE, BACK_POSE))
                .setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(50))
                .build();

        pathReturn = PedroComponent.follower().pathBuilder()
                .addPath(new BezierLine(BACK_POSE, START_POSE))
                .setLinearHeadingInterpolation(Math.toRadians(50), Math.toRadians(40))
                .build();
    }

    @Override
    public void onStartButtonPressed() {
        new SequentialGroup(
                new FollowPath(pathBack, true),        // 後退
                CorrectionCommand.correct(),                  // Limelight で補正
                new ParallelDeadlineGroup(                    // 射撃
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(pathReturn, true)         // 初期位置に戻る
        ).schedule();
    }

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower(), LimelightSubsystem.INSTANCE.getLimelightPose());
    }
}
