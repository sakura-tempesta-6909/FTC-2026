package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

import java.util.List;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.DriverControlledCommand;

@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private final ElapsedTime loopTimer = new ElapsedTime();
    private int lastSnapshotSize = 0;

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

    }

    @Override
    public void onStartButtonPressed() {
        DriverControlledCommand driverControlled = new PedroDriverControlled(
                Gamepads.gamepad2().leftStickY(),
                Gamepads.gamepad2().leftStickX(),
                Gamepads.gamepad2().rightStickX()
        );
        driverControlled.schedule();
        Gamepads.gamepad1().x()
                .whenTrue(ShooterCommand.shootArtifacts())
                .whenBecomesFalse(ShooterCommand.stopAll());

        Gamepads.gamepad1().y()
                .whenTrue(ShooterCommand.reverseArtifacts())
                .whenBecomesFalse(ShooterCommand.stopAll());

        Gamepads.gamepad1().a()
                .whenTrue(IntakeCommand.intake())
                .whenBecomesFalse(IntakeCommand.stopAll());

        Gamepads.gamepad1().b()
                .whenTrue(IntakeCommand.outtake())
                .whenBecomesFalse(IntakeCommand.stopAll());

    }

    @Override
    public void onUpdate() {
        //実行時間表示
        double dt = loopTimer.seconds();
        loopTimer.reset();

        //実行しているコマンドを表示
        List<String> snapshot = CommandManager.INSTANCE.snapshot();
        int currentSize = snapshot.size();
        int fromIndex = Math.min(lastSnapshotSize, currentSize);
        List<String> running = snapshot.subList(fromIndex, currentSize);
        lastSnapshotSize = currentSize;

        panelsTelemetry.getTelemetry().addData("dt", dt);
        panelsTelemetry.getTelemetry().addData("running", String.join(", ", running));
        panelsTelemetry.getTelemetry().update();
    }
}
