package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link IntakeCommand} の lifecycle 検証。
 * 「永続実行 + setStop で停止」の核心パターンが守られているかをテストする。
 */
public class IntakeCommandTest extends SubsystemTestBase {

    @Test
    public void intake_metadata() {
        Command cmd = IntakeCommand.intake();
        assertEquals("intake", cmd.name());
        assertTrue("interruptible", cmd.getInterruptible());
        assertTrue("Intake を requires している",
                cmd.getRequirements().contains(IntakeSubsystem.INSTANCE));
        // Intake のみで Feeder は触らない
        assertEquals("requires は1個", 1, cmd.getRequirements().size());
    }

    @Test
    public void intake_isPerpetual() {
        Command cmd = IntakeCommand.intake();
        cmd.start();
        assertFalse("intake() は永続コマンド (isDone == false)", cmd.isDone());
    }

    @Test
    public void intake_startCallsSetPowerIntake() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        IntakeCommand.intake().start();

        verify(motor).setPower(Const.Intake.Power.INTAKE);
    }

    @Test
    public void intake_stopCallsSetPowerStop() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = IntakeCommand.intake();
        cmd.start();
        clearInvocations(motor);

        cmd.stop(true);  // 中断

        verify(motor).setPower(Const.Intake.Power.STOP);
    }

    @Test
    public void intake_naturalCompletionAlsoStops() {
        // 永続コマンドだが、自然完了 (interrupted=false) でも setStop で安全に止まることを確認
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = IntakeCommand.intake();
        cmd.start();
        clearInvocations(motor);

        cmd.stop(false);  // 自然完了

        verify(motor).setPower(Const.Intake.Power.STOP);
    }

    @Test
    public void outtake_metadata() {
        Command cmd = IntakeCommand.outtake();
        assertEquals("outtake", cmd.name());
        assertTrue(cmd.getInterruptible());
        assertTrue(cmd.getRequirements().contains(IntakeSubsystem.INSTANCE));
        assertEquals(1, cmd.getRequirements().size());
    }

    @Test
    public void outtake_startCallsSetPowerReverse() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        IntakeCommand.outtake().start();

        verify(motor).setPower(Const.Intake.Power.REVERSE);
    }

    @Test
    public void outtake_stopCallsSetPowerStop() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        Command cmd = IntakeCommand.outtake();
        cmd.start();
        clearInvocations(motor);

        cmd.stop(true);

        verify(motor).setPower(Const.Intake.Power.STOP);
    }
}
