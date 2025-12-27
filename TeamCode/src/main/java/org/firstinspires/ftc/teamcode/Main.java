package org.firstinspires.ftc.teamcode;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
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

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private final ElapsedTime loopTimer = new ElapsedTime();
    private int lastSnapshotSize = 0;

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
}