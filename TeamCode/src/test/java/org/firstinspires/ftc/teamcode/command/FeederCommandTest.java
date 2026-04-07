package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link FeederCommand} の lifecycle 検証。
 */
public class FeederCommandTest extends SubsystemTestBase {

    @Test
    public void feed_metadata() {
        Command cmd = FeederCommand.feed();
        assertEquals("feed", cmd.name());
        assertTrue(cmd.getInterruptible());
        assertTrue(cmd.getRequirements().contains(FeederSubsystem.INSTANCE));
        assertEquals(1, cmd.getRequirements().size());
    }

    @Test
    public void feed_isPerpetual() {
        // start() を呼ばずに isDone だけ確認 (Supplier<Boolean>{false} なので)
        Command cmd = FeederCommand.feed();
        assertFalse(cmd.isDone());
    }

    @Test
    public void feed_startThenStop() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = FeederCommand.feed();

        clearInvocations(motor);
        cmd.start();
        verify(motor).setPower(Const.Feeder.Power.FEED);

        clearInvocations(motor);
        cmd.stop(true);
        verify(motor).setPower(Const.Feeder.Power.STOP);
    }

    @Test
    public void weakFeed_startThenStop() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = FeederCommand.weakFeed();

        clearInvocations(motor);
        cmd.start();
        verify(motor).setPower(Const.Feeder.Power.WEAKFEED);

        clearInvocations(motor);
        cmd.stop(true);
        verify(motor).setPower(Const.Feeder.Power.STOP);
    }

    @Test
    public void retract_startThenStop() {
        FeederSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = FeederCommand.retract();

        clearInvocations(motor);
        cmd.start();
        verify(motor).setPower(Const.Feeder.Power.RETRACT);

        clearInvocations(motor);
        cmd.stop(true);
        verify(motor).setPower(Const.Feeder.Power.STOP);
    }
}
