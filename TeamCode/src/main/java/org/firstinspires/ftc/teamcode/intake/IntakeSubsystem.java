package org.firstinspires.ftc.teamcode.intake;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;

public class IntakeSubsystem implements Subsystem {
    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();
    private final MotorEx intakeMotor = new MotorEx("IntakeMotor");
    public final Command intake = new SetPower(intakeMotor, 1.0).requires(this);
    public final Command stop = new SetPower(intakeMotor, 0.0).requires(this);

    @Override
    public void initialize() {
        intakeMotor.setPower(0.0);
    }

}




