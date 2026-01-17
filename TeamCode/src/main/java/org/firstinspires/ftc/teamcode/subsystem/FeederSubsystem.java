package org.firstinspires.ftc.teamcode.subsystem;

import org.firstinspires.ftc.teamcode.config.Const;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

public class FeederSubsystem implements Subsystem {
    public enum FeederState {
        FEED,
        STOP,
        RETRACT
    }

    public static final FeederSubsystem INSTANCE = new FeederSubsystem();
    private MotorEx feederMotor;
    private FeederState state = FeederState.STOP;

    public void setState(FeederState state) {
        this.state = state;
    }


    @Override
    public void initialize() {
        feederMotor = new MotorEx(Const.Feeder.Motor.NAME);
        feederMotor.reverse();
    }

    @Override
    public void periodic() {
        switch (state) {
            case FEED -> feederMotor.setPower(Const.Feeder.Power.FEED);
            case RETRACT -> feederMotor.setPower(Const.Feeder.Power.RETRACT);
            case STOP -> feederMotor.setPower(0.0);
        }
    }


}
