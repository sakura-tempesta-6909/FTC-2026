package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;
import org.firstinspires.ftc.teamcode.config.Const;

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

    public Command feed() {
        return new SetPower(feederMotor, Const.Feeder.Power.FEED).requires(this).named("feederFeed");
    }

    public Command retract() {
        return new SetPower(feederMotor, Const.Feeder.Power.RETRACT).requires(this).named("feederRetract");
    }

    public Command stop() {
        return new SetPower(feederMotor, 0.0).requires(this).named("feederStop");
    }

}
