package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.TestPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;

@Autonomous(name = "NextFTC Autonomous Program Java")
@Configurable
public class TestAuto extends FTCBaseOpMode {
    private TestPath testPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(122.815, 126.156, Math.toRadians(36)));
        PedroComponent.follower().setMaxPower(0.4);
        testPath = new TestPath(PedroComponent.follower());
    }


    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(testPath.Path1),
                new FollowPath(testPath.Path2),
                new ParallelDeadlineGroup(
                        new FollowPath(testPath.Path3, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(testPath.Path4),
                new FollowPath(testPath.Path5),
                new ParallelDeadlineGroup(
                        new FollowPath(testPath.Path6, false, 0.5),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(testPath.Path7)
        );
    }

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower(), LimelightSubsystem.INSTANCE.getLimelightPose());
    }

}
