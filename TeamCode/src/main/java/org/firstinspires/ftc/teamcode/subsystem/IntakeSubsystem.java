package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * インテーク (取り込みローラー) を制御するサブシステム。
 * <p>
 * 状態を内部に持たず、{@link #setPower(double)} / {@link #stop()} で
 * モータを直接操作する薄いラッパー。
 * Command ベースの設計に従い、「いつ何を動かすか」は Command 側で管理する。
 */
public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx intakeMotor;

    private IntakeSubsystem() {}

    @Override
    public void initialize() {
        intakeMotor = new MotorEx(Const.Intake.Motor.NAME);
        intakeMotor.brakeMode();
//        intakeMotor.reverse();
        intakeMotor.setPower(Const.Intake.Power.INTAKE);
    }

    /** 任意のパワーをセットする。{@link Const.Intake.Power} の値を渡すこと。 */
    public void setPower(double power) {
        intakeMotor.setPower(power);
    }

    /** インテークを停止する。 */
    public void stop() {
        intakeMotor.setPower(Const.Intake.Power.STOP);
    }
}
