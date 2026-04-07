package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link FeederSubsystem} の単体テスト。
 */
public class FeederSubsystemTest extends SubsystemTestBase {

    @Test
    public void initialize_setsReverseAndBrakeAndStopPower() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);

        verify(motor).reverse();
        verify(motor).brakeMode();
        verify(motor).setPower(Const.Feeder.Power.STOP);
    }

    @Test
    public void feed_appliesFeedPower() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        FeederSubsystem.INSTANCE.feed();
        verify(motor).setPower(Const.Feeder.Power.FEED);
    }

    @Test
    public void weakFeed_appliesWeakFeedPower() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        FeederSubsystem.INSTANCE.weakFeed();
        verify(motor).setPower(Const.Feeder.Power.WEAKFEED);
    }

    @Test
    public void retract_appliesRetractPower() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        FeederSubsystem.INSTANCE.retract();
        verify(motor).setPower(Const.Feeder.Power.RETRACT);
    }

    @Test
    public void stop_appliesStopPower() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        FeederSubsystem.INSTANCE.stop();
        verify(motor).setPower(Const.Feeder.Power.STOP);
    }

    @Test
    public void setPower_appliesArbitraryValue() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        FeederSubsystem.INSTANCE.setPower(0.42);
        verify(motor).setPower(0.42);
    }
}
