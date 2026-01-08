package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import dev.nextftc.hardware.powerable.SetPower;
import org.firstinspires.ftc.teamcode.config.Const;

public class FeederSubsystem implements Subsystem {
    public static final FeederSubsystem INSTANCE = new FeederSubsystem();
    private MotorEx feederMotor;

    @Override
    public void initialize() {
        feederMotor = new MotorEx(Const.Motor.FEEDER);
        feederMotor.reverse();
    }

    public Command feed() {
        return new SetPower(feederMotor, Const.Feeder.FEED_POWER).requires(this).named("feederFeed");
    }

    public Command retract() {
        return new SetPower(feederMotor, Const.Feeder.RETRACT_POWER).requires(this).named("feederRetract");
    }

    public Command stop() {
        return new SetPower(feederMotor, 0.0).requires(this).named("feederStop");
    }

}
