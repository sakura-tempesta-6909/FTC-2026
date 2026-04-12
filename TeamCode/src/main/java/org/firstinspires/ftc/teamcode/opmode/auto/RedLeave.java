package org.firstinspires.ftc.teamcode.opmode.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.path.RedLeavePath;

@Autonomous(name = "Red Leave")
@Configurable
public class RedLeave extends FTCBaseOpMode {
    private RedLeavePath redLeavePath;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(86.000, 8.000, Math.toRadians(90)));
        redLeavePath = new RedLeavePath(PedroComponent.follower());
    }


    @Override
    public void onStartButtonPressed() {
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(redLeavePath.Path1, true, 0.3)
        );
    }

    @Override
    public void onUpdate() {
        updateDriverHubTelemetry(driverStationTelemetry);
        Drawing.drawDebug(PedroComponent.follower());
    }
}