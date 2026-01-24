package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.telemetry.PanelsTelemetry;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;


public class IntakeSubsystem implements Subsystem {
    public enum IntakeState {
        INTAKE,
        STOP,
        REVERSE
    }

    private IntakeSubsystem.IntakeState state = IntakeSubsystem.IntakeState.STOP;

    public void setState(IntakeSubsystem.IntakeState state) {
        this.state = state;
    }

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx intakeMotor;


    @Override
    public void initialize() {
        intakeMotor = new MotorEx(Const.Intake.Motor.NAME);
    }

    @Override
    public void periodic() {
        switch (state) {
            case INTAKE -> intakeMotor.setPower(Const.Intake.Power.INTAKE);
            case REVERSE -> intakeMotor.setPower(Const.Intake.Power.REVERSE);
            case STOP -> intakeMotor.setPower(0.0);
        }
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Intake State", state.toString());
    }

}
