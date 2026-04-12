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
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.RedGoalPath;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;

@Autonomous(name = "Red Goal")
@Configurable
public class RedGoal extends FTCBaseOpMode {
    private RedGoalPath redGoalPath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(117.386, 130.854, Math.toRadians(40)));
        redGoalPath = new RedGoalPath(PedroComponent.follower());
    }


    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(redGoalPath.Path1, true, 0.3),
                // endAfter は ParallelRaceGroup のバグで永続コマンドに効かないため
                // ParallelDeadlineGroup(Delay, routine) を使う
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootContinuous()
                ),
                new FollowPath(redGoalPath.Path2),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path3, false, 0.3),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(redGoalPath.Path4),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootWithRetract()
                ),
                new FollowPath(redGoalPath.Path5),
                new ParallelDeadlineGroup(
                        new FollowPath(redGoalPath.Path6, false, 0.3),
                        IntakeRoutine.intakeWithWeakFeed()
                ),
                new FollowPath(redGoalPath.Path7),
                new ParallelDeadlineGroup(
                        new Delay(SHOOT_DURATION_SECONDS),
                        ShootingRoutine.shootWithRetract()
                ),
                new FollowPath(redGoalPath.Path8)
        );
    }

    private static final double SHOOT_DURATION_SECONDS = 3.0;

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower());
    }
}