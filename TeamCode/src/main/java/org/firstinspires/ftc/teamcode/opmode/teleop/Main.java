package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private final ElapsedTime loopTimer = new ElapsedTime();

    // TeleOp 開始時のロボット初期位置 (フィールド座標系)
    private static final Pose STARTING_POSE = new Pose(122.815, 124.882, Math.toRadians(36));

    public Main() {
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
        PedroComponent.follower().setStartingPose(STARTING_POSE);
        Drawing.init();
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

        // Heading リセット (現在位置を維持したまま向きだけ 0 に)
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

        panelsTelemetry.getTelemetry().addData("dt", dt);
        panelsTelemetry.getTelemetry().addData("running",
                String.join(", ", CommandManager.INSTANCE.snapshot()));
        panelsTelemetry.getTelemetry().update();

        // Pedro 位置 + Limelight 推定位置を描画
        Drawing.drawDebug(PedroComponent.follower(), ShooterSubsystem.INSTANCE.getLimelightPose());
    }
}
