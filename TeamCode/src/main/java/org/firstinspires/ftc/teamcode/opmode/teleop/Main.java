package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.pedropathing.util.Timer;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.ParallelRaceGroup;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.hardware.driving.DriverControlledCommand;

import org.firstinspires.ftc.teamcode.command.FeederCommand;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.SlewRateLimiter;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;

@TeleOp(name = "Main")
public class Main extends FTCBaseOpMode {

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private static final Pose STARTING_POSE = new Pose(122.815, 124.882, Math.toRadians(36));

    private static final double DRIVE_SLEW_PER_SEC = 8.0;
    private static final double STRAFE_SLEW_PER_SEC = 8.0;
    private static final double TURN_SLEW_PER_SEC = 10.0;

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(STARTING_POSE);
    }

    @Override
    public void onStartButtonPressed() {
        DriverControlledCommand driverControlled = new PedroDriverControlled(
                Gamepads.gamepad1().leftStickY().negate(),
                Gamepads.gamepad1().leftStickX().negate(),
                Gamepads.gamepad1().rightStickX().negate(),
                false
        );
        Gamepads.gamepad1().rightBumper()
                .whenTrue(() -> driverControlled.setScalar(0.2))
                .whenFalse(() -> driverControlled.setScalar(1.0));

        driverControlled.schedule();

        // ===== ホールド系バインディング =====
        Command shootCmd = ShootingRoutine.shootContinuous();
        Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
                .whenBecomesTrue(new ParallelGroup(
                        FeederCommand.feed(),
                        ShooterCommand.holdRpm(),
                        IntakeCommand.intake()
                ))
                .whenFalse(new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        IntakeCommand.intake())
                );

        Gamepads.gamepad1().b()
                .whenBecomesTrue(new ParallelRaceGroup(
                        new Delay(0.5),
                        FeederCommand.feed()

                ))
                .whenBecomesFalse(FeederCommand.stop());


        Gamepads.gamepad2().options()
                .whenBecomesTrue(new InstantCommand(() -> {
                    Pose current = PedroComponent.follower().getPose();
                    PedroComponent.follower().setPose(
                            new Pose(current.getX(), current.getY(), Math.toRadians(0)));
                }));
    }

    @Override
    public void onUpdate() {
        double dt = logLoopTimer.seconds();

        // Panels テレメトリ
        var panels = panelsTelemetry.getTelemetry();
        panels.addData("[システム] ループ", String.format("%.1fms", dt * 1000));
        panels.addData("[システム] 実行中",
                String.join(", ", CommandManager.INSTANCE.snapshot()));
        panels.update();

        // Driver Hub テレメトリ (元の FTC SDK テレメトリに書き込む)
        updateDriverHubTelemetry(driverStationTelemetry);

        // KoalaLog (wpilog への時系列記録) — logLoopTimer をリセット
        logTick();

        Drawing.drawDebug(PedroComponent.follower(), LimelightSubsystem.INSTANCE.getLimelightPose());
    }
}
