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
import org.firstinspires.ftc.teamcode.path.RedGoal30030Path;
import org.firstinspires.ftc.teamcode.path.RedNear1stackPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Red for 30030")
@Configurable
public class RedGoal30030 extends FTCBaseOpMode {
    private RedGoal30030Path red30030Path;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(112.067, 135.348, Math.toRadians(0)));
        red30030Path = new RedGoal30030Path(PedroComponent.follower());
        Drawing.drawDebug(PedroComponent.follower());
    }

    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                // 後退しながらスピンアップ (移動時間でRPMを上げておく)
                new FollowPath(red30030Path.Path1, false, 1.0),
//                CorrectionCommand.correct(), // Limelight で位置補正
                // スピンアップ済みなのですぐ射撃開始
                new Delay(15),
                new ParallelDeadlineGroup(
                new FollowPath(red30030Path.Path2,false,1.0),
                ShooterCommand.spinUpForPath()
                ),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(red30030Path.Path3, false, 1.0)
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 1.8;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        logTick();
        Drawing.drawDebug(PedroComponent.follower(), null,
                red30030Path.Path1, red30030Path.Path2, red30030Path.Path3);
    }
}
