package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;

import java.util.List;

@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    public Main() {
        addComponents(
                new SubsystemComponent(IntakeSubsystem.INSTANCE,ShooterSubsystem.INSTANCE,FeederSubsystem.INSTANCE)
        );
    }

    @Override
    public void onStartButtonPressed() {
        Gamepads.gamepad1().x()
                .whenTrue(IntakeCommand.intakeArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
        Gamepads.gamepad1().y()
                .whenTrue(IntakeCommand.reverseArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void onStop() {
        IntakeCommand.stopAll();
    }
}
