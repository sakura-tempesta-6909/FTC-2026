package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;

import java.util.List;


@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    private PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    public Main() {
        addComponents(
                new SubsystemComponent(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed() {
        Gamepads.gamepad1().x()
                .whenTrue(IntakeCommand.intakeArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
        Gamepads.gamepad1().y()
                .whenTrue(IntakeCommand.outtakeArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
    }

    @Override
    public void onUpdate() {
        List<String> commands = CommandManager.INSTANCE.snapshot();
        panelsTelemetry.getTelemetry().addData("command", commands.get(commands.size() - 1));
        panelsTelemetry.getTelemetry().update(telemetry);
    }
}