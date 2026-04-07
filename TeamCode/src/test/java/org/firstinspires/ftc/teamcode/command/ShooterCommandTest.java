package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * {@link ShooterCommand} の構造検証。
 * <p>
 * spinUp / spinUpReverse / holdRpm の各コマンドが想定どおりの requires と
 * 中断挙動 (永続 vs 自然完了型) を持っているかを確認する。
 */
public class ShooterCommandTest extends SubsystemTestBase {

    @Test
    public void spinUp_metadata() {
        Command cmd = ShooterCommand.spinUp();
        assertEquals("spinUp", cmd.name());
        assertTrue(cmd.getInterruptible());
        assertTrue(cmd.getRequirements().contains(ShooterSubsystem.INSTANCE));
        assertEquals(1, cmd.getRequirements().size());
    }

    @Test
    public void spinUpReverse_metadata() {
        Command cmd = ShooterCommand.spinUpReverse();
        assertEquals("spinUpReverse", cmd.name());
        assertTrue(cmd.getInterruptible());
        assertTrue(cmd.getRequirements().contains(ShooterSubsystem.INSTANCE));
    }

    @Test
    public void holdRpm_metadata() {
        Command cmd = ShooterCommand.holdRpm();
        assertEquals("holdRpm", cmd.name());
        assertTrue(cmd.getInterruptible());
        assertTrue(cmd.getRequirements().contains(ShooterSubsystem.INSTANCE));
    }

    @Test
    public void holdRpm_isPerpetual() {
        Command cmd = ShooterCommand.holdRpm();
        // start を呼ばずとも isDone は false (始める前から永続)
        assertFalse("holdRpm は永続コマンドであるべき", cmd.isDone());
    }
}
