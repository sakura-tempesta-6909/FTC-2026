package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;
import org.firstinspires.ftc.teamcode.config.Const;

public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx motor;


    @Override
    public void initialize() {
        motor = new MotorEx(Const.Intake.Motor.NAME);
    }

    public Command intake() {
        return new SetPower(motor, Const.Intake.Power.INTAKE).requires(this).named("intake");

    }

    public Command retract() {
        return new SetPower(motor, Const.Intake.Power.REVERSE).requires(this).named("retract");
    }

    public Command stop() {
        return new SetPower(motor, 0.0).requires(this).named("stop");
    }

}