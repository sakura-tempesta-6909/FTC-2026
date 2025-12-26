package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.intake.IntakeSubsystem;


@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    public Main() {
        addComponents(
                new SubsystemComponent(IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onStartButtonPressed() {
        Gamepads.gamepad1().x()
                .whenBecomesTrue(IntakeSubsystem.INSTANCE.intake)
                .whenFalse(IntakeSubsystem.INSTANCE.stop);
    }
}