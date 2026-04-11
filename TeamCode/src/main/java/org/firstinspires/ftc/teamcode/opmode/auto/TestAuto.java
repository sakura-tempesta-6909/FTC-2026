package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.path.TestPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@Autonomous(name = "NextFTC Autonomous Program Java")
@Configurable
public class TestAuto extends NextFTCOpMode {
    private TestPath testPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    public TestAuto() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(122.815, 126.156, Math.toRadians(36)));
        PedroComponent.follower().setMaxPower(0.4);
        testPath = new TestPath(PedroComponent.follower());
        Drawing.init();
        Drawing.drawDebug(PedroComponent.follower());
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
        Drawing.drawDebug(PedroComponent.follower(), ShooterSubsystem.INSTANCE.getLimelightPose());
    }

}
