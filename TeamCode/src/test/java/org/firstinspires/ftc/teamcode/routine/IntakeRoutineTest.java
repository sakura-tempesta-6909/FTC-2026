package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link IntakeRoutine} の構造とキャンセル動作の検証。
 */
public class IntakeRoutineTest extends SubsystemTestBase {

    @Test
    public void intakeWithWeakFeed_isParallelGroup() {
        Command routine = IntakeRoutine.intakeWithWeakFeed();
        assertTrue(routine instanceof ParallelGroup);
    }

    @Test
    public void intakeWithWeakFeed_aggregatesIntakeAndFeederRequirements() {
        Command routine = IntakeRoutine.intakeWithWeakFeed();
        assertTrue(routine.getRequirements().contains(IntakeSubsystem.INSTANCE));
        assertTrue(routine.getRequirements().contains(FeederSubsystem.INSTANCE));
        assertEquals(2, routine.getRequirements().size());
    }

    @Test
    public void intakeWithWeakFeed_startThenCancel_stopsBothMotors() {
        initAllSubsystems();
        MotorEx intake = intakeMotor();
        MotorEx feeder = feederMotor();

        Command routine = IntakeRoutine.intakeWithWeakFeed();

        clearInvocations(intake, feeder);
        routine.start();
        verify(intake).setPower(Const.Intake.Power.INTAKE);
        verify(feeder).setPower(Const.Feeder.Power.WEAK_FEED);

        clearInvocations(intake, feeder);
        routine.stop(true);
        verify(intake).setPower(Const.Intake.Power.STOP);
        verify(feeder).setPower(Const.Feeder.Power.STOP);
    }

    @Test
    public void outtakeWithRetract_startThenCancel_stopsBothMotors() {
        initAllSubsystems();
        MotorEx intake = intakeMotor();
        MotorEx feeder = feederMotor();

        Command routine = IntakeRoutine.outtakeWithRetract();

        clearInvocations(intake, feeder);
        routine.start();
        verify(intake).setPower(Const.Intake.Power.REVERSE);
        verify(feeder).setPower(Const.Feeder.Power.RETRACT);

        clearInvocations(intake, feeder);
        routine.stop(true);
        verify(intake).setPower(Const.Intake.Power.STOP);
        verify(feeder).setPower(Const.Feeder.Power.STOP);
    }
}
