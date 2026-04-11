package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import org.firstinspires.ftc.teamcode.testutil.SubsystemTestBase;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.verify;

/**
 * {@link ShootingRoutine} の構造とキャンセル動作の検証。
 * <p>
 * 「Routine がキャンセルされた瞬間に全 Subsystem が STOP に戻る」
 * という設計上の鉄則をテストとして固める。
 * これは shoot バグの再発を防ぐためのリグレッションテストでもある。
 */
public class ShootingRoutineTest extends SubsystemTestBase {

    @Test
    public void shootWithRetract_isSequentialGroup() {
        Command routine = ShootingRoutine.shootWithRetract();
        assertTrue("最上位は SequentialGroup", routine instanceof SequentialGroup);
    }

    @Test
    public void shootWithRetract_aggregatesAllThreeRequirements() {
        Command routine = ShootingRoutine.shootWithRetract();
        assertTrue("Shooter を requires",
                routine.getRequirements().contains(ShooterSubsystem.INSTANCE));
        assertTrue("Feeder を requires",
                routine.getRequirements().contains(FeederSubsystem.INSTANCE));
        assertTrue("Intake を requires",
                routine.getRequirements().contains(IntakeSubsystem.INSTANCE));
        assertEquals("Subsystem は3つ", 3, routine.getRequirements().size());
    }

    @Test
    public void shootContinuous_isSequentialGroup() {
        Command routine = ShootingRoutine.shootContinuous();
        assertTrue(routine instanceof SequentialGroup);
    }

    @Test
    public void shootContinuous_aggregatesAllThreeRequirements() {
        Command routine = ShootingRoutine.shootContinuous();
        assertTrue(routine.getRequirements().contains(ShooterSubsystem.INSTANCE));
        assertTrue(routine.getRequirements().contains(FeederSubsystem.INSTANCE));
        assertTrue(routine.getRequirements().contains(IntakeSubsystem.INSTANCE));
    }

    @Test
    public void shootWithRetract_isInterruptible() {
        Command routine = ShootingRoutine.shootWithRetract();
        assertTrue("Routine 全体は中断可能であるべき", routine.getInterruptible());
    }

    /**
     * <strong>核心テスト</strong>: 連射フェーズで Routine をキャンセルしたとき、
     * Shooter / Feeder / Intake の全モータが STOP に戻ることを検証する。
     * <p>
     * これがまさに先日の shoot バグで動かなかったケース。
     */
    @Test
    public void shootWithRetract_cancelDuringFirePhase_stopsAllMotors() {
        initAllSubsystems();
        MotorEx intake = intakeMotor();
        MotorEx feeder = feederMotor();
        MotorEx shooter = shooterMotor();

        Command routine = ShootingRoutine.shootWithRetract();
        // 内部の SequentialGroup は [retract.endAfter, spinUp, ParallelGroup(holdRpm, intake, feed)]
        // 連射フェーズに到達するには 3 つ目の child まで進める必要がある。
        // ここでは深いシミュレーションは避け、SequentialGroup の children を直接消費する形で
        // 連射フェーズの ParallelGroup を取得し、その start/stop を検証する。
        SequentialGroup seq = (SequentialGroup) routine;
        // 最後の child が ParallelGroup
        Command fireParallel = seq.getCommands()[seq.getCommands().length - 1];

        // 連射フェーズだけ起動 → モータが回ることを確認
        clearInvocations(intake, feeder, shooter);
        fireParallel.start();
        verify(intake).setPower(Const.Intake.Power.INTAKE);
        verify(feeder).setPower(Const.Feeder.Power.FEED);
        // holdRpm 自体は target を変えないので shooter モータへの追加呼び出しは無し

        // キャンセル → 全モータが STOP に戻る
        clearInvocations(intake, feeder, shooter);
        fireParallel.stop(true);

        verify(intake).setPower(Const.Intake.Power.STOP);
        verify(feeder).setPower(Const.Feeder.Power.STOP);
        // holdRpm.setStop は ShooterSubsystem.stop() を呼び、それが
        // controller.setGoal(KineticState(0, 0)) を呼ぶ。モータへの直接 setPower は
        // periodic() でしか起きないため、ここでは検証しない。
    }

    /**
     * 引き戻しフェーズで中断 → Feeder が STOP に戻るかの検証。
     */
    @Test
    public void shootWithRetract_cancelDuringRetractPhase_stopsFeeder() {
        initAllSubsystems();
        MotorEx feeder = feederMotor();

        Command routine = ShootingRoutine.shootWithRetract();
        SequentialGroup seq = (SequentialGroup) routine;
        Command retractPhase = seq.getCommands()[0];  // retract().endAfter(...)

        clearInvocations(feeder);
        retractPhase.start();
        verify(feeder).setPower(Const.Feeder.Power.RETRACT);

        clearInvocations(feeder);
        retractPhase.stop(true);
        verify(feeder).setPower(Const.Feeder.Power.STOP);
    }
}
