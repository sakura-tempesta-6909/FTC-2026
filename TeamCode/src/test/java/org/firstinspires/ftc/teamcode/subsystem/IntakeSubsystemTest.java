package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link IntakeSubsystem} の単体テスト。
 * mockConstruction で生成された MotorEx モックに正しいパワーが流れるかを検証する。
 */
public class IntakeSubsystemTest extends SubsystemTestBase {

    @Test
    public void initialize_setsBrakeAndReverseAndStopPower() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);

        verify(motor).brakeMode();
        verify(motor).reverse();
        verify(motor).setPower(Const.Intake.Power.STOP);
    }

    @Test
    public void setPower_appliesGivenValue() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);  // initialize 内の呼び出しを忘れる

        IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.INTAKE);
        verify(motor).setPower(Const.Intake.Power.INTAKE);
    }

    @Test
    public void setPower_reverse() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        IntakeSubsystem.INSTANCE.setPower(Const.Intake.Power.REVERSE);
        verify(motor).setPower(Const.Intake.Power.REVERSE);
    }

    @Test
    public void stop_setsPowerToStopConstant() {
        IntakeSubsystem.INSTANCE.initialize();
        MotorEx motor = motorMock.constructed().get(0);
        clearInvocations(motor);

        IntakeSubsystem.INSTANCE.stop();
        verify(motor).setPower(Const.Intake.Power.STOP);
    }
}
