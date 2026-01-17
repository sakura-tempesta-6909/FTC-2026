package org.firstinspires.ftc.teamcode.subsystem;

import org.firstinspires.ftc.teamcode.config.Const;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx intakeMotor;


    @Override
    public void initialize() {
        intakeMotor = new MotorEx(Const.Intake.Motor.NAME);
    }

    public Command intake() {
        return new SetPower(intakeMotor, Const.Intake.Power.INTAKE).requires(this).named("intake");

    }

    public Command retract() {
        return new SetPower(intakeMotor, Const.Intake.Power.REVERSE).requires(this).named("retract");
    }

    public Command stop() {
        return new SetPower(intakeMotor, 0.0).requires(this).named("stop");
    }

}
