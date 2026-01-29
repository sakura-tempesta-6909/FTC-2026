package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;

public class FeederSubsystem implements Subsystem {
    public enum FeederState {
        FEED,
        STOP,
        WEAKFEED,
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
        feederMotor.brakeMode();

        // 状態をリセット
        state = FeederState.STOP;
    }

    @Override
    public void periodic() {
        switch (state) {
            case FEED -> feederMotor.setPower(Const.Feeder.Power.FEED);
            case WEAKFEED -> feederMotor.setPower(Const.Feeder.Power.WEAKFEED);
            case RETRACT -> feederMotor.setPower(Const.Feeder.Power.RETRACT);
            case STOP -> feederMotor.setPower(0.0);
        }
    }


}
