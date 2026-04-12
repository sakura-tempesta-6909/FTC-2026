package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.opmode.FTCBaseOpMode;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;

@TeleOp(name = "Main")
public class Main extends FTCBaseOpMode {

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private final ElapsedTime loopTimer = new ElapsedTime();

    private static final Pose STARTING_POSE = new Pose(122.815, 124.882, Math.toRadians(36));

    @Override
    public void onInit() {
        super.onInit();
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(STARTING_POSE);
    }

    @Override
    public void onStartButtonPressed() {
        DriverControlledCommand driverControlled = new PedroDriverControlled(
                Gamepads.gamepad2().leftStickY().negate(),
                Gamepads.gamepad2().leftStickX().negate(),
                Gamepads.gamepad2().rightStickX().negate(),
                false
        );
        Gamepads.gamepad2().rightBumper()
                .whenTrue(() -> driverControlled.setScalar(0.2))
                .whenFalse(() -> driverControlled.setScalar(1.0));

        driverControlled.schedule();

        // ===== ホールド系バインディング =====
        Command shootCmd = ShootingRoutine.shootWithRetract();
        Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
                .whenBecomesTrue(shootCmd::schedule)
                .whenBecomesFalse(shootCmd::cancel);

        Command reverseCmd = ShooterCommand.spinUpReverse();
        Gamepads.gamepad1().y().and(Gamepads.gamepad1().x().not())
                .whenBecomesTrue(reverseCmd::schedule)
                .whenBecomesFalse(reverseCmd::cancel);

        Command intakeCmd = IntakeRoutine.intakeWithWeakFeed();
        Gamepads.gamepad1().a().and(Gamepads.gamepad1().b().not())
                .whenBecomesTrue(intakeCmd::schedule)
                .whenBecomesFalse(intakeCmd::cancel);

        Command outtakeCmd = IntakeRoutine.outtakeWithRetract();
        Gamepads.gamepad1().b().and(Gamepads.gamepad1().a().not())
                .whenBecomesTrue(outtakeCmd::schedule)
                .whenBecomesFalse(outtakeCmd::cancel);

        Gamepads.gamepad2().options()
                .whenBecomesTrue(new InstantCommand(() -> {
                    Pose current = PedroComponent.follower().getPose();
                    PedroComponent.follower().setPose(
                            new Pose(current.getX(), current.getY(), Math.toRadians(0)));
                }));
    }

    @Override
    public void onUpdate() {
        double dt = loopTimer.seconds();
        loopTimer.reset();

        // Panels テレメトリ
        var panelsTelemetry = this.panelsTelemetry.getTelemetry();
        panelsTelemetry.addData("[システム] ループ", String.format("%.1fms", dt * 1000));
        panelsTelemetry.addData("[システム] 実行中",
                String.join(", ", CommandManager.INSTANCE.snapshot()));
        panelsTelemetry.update();

        // Driver Hub テレメトリ (元の FTC SDK テレメトリに書き込む)
        updateDriverHubTelemetry(driverStationTelemetry);

        Drawing.drawDebug(PedroComponent.follower(), LimelightSubsystem.INSTANCE.getLimelightPose());
    }
}
