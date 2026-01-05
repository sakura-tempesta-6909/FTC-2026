package org.firstinspires.ftc.teamcode.opmode.teleop;


import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

import java.util.List;

import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;

@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private final ElapsedTime loopTimer = new ElapsedTime();
    private int lastSnapshotSize = 0;


    public Main() {
        addComponents(
                new SubsystemComponent(IntakeSubsystem.INSTANCE,ShooterSubsystem.INSTANCE,FeederSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed() {
        Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
                .whenTrue(IntakeCommand.intakeArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
        Gamepads.gamepad1().y().and(Gamepads.gamepad1().x().not())
                .whenTrue(IntakeCommand.intakeReverseArtifacts())
                .whenBecomesFalse(IntakeCommand.stopAll());
        Gamepads.gamepad1().a().and(Gamepads.gamepad1().b().not())
                .whenTrue(ShooterCommand.shootArtifacts())
                .whenBecomesFalse(ShooterCommand.stopAll());
        Gamepads.gamepad1().b().and(Gamepads.gamepad1().a().not())
                .whenTrue(ShooterCommand.reverseArtifacts())
                .whenBecomesFalse(ShooterCommand.stopAll());
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
        panelsTelemetry.getTelemetry().update(telemetry);

    }

    @Override
    public void onStop() {
        IntakeCommand.stopAll().schedule();
        ShooterCommand.stopAll().schedule();
    }
}
