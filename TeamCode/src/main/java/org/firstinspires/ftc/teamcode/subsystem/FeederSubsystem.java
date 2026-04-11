package org.firstinspires.ftc.teamcode.subsystem;

import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * フィーダー (ボールをシューターに送り込む機構) を制御するサブシステム。
 * <p>
 * 状態を内部に持たず、Command 側からパワーを直接指定する薄いラッパー。
 * 主要なパワーレベル (FEED / WEAK_FEED / RETRACT / STOP) ごとに
 * 便利メソッドを提供している。
 */
public class FeederSubsystem implements Subsystem {

    public static final FeederSubsystem INSTANCE = new FeederSubsystem();

    private MotorEx feederMotor;

    private FeederSubsystem() {}

    @Override
    public void initialize() {
        feederMotor = new MotorEx(Const.Feeder.Motor.NAME);
        feederMotor.reverse();
        feederMotor.brakeMode();
        feederMotor.setPower(Const.Feeder.Power.STOP);
    }

    /** 任意のパワーをセットする。{@link Const.Feeder.Power} の値を渡すこと。 */
    public void setPower(double power) {
        feederMotor.setPower(power);
    }

    /** 通常の送り込み速度で前進。 */
    public void feed() {
        feederMotor.setPower(Const.Feeder.Power.FEED);
    }

    /** 弱い送り込み速度で前進 (インテーク中の待機時など)。 */
    public void weakFeed() {
        feederMotor.setPower(Const.Feeder.Power.WEAK_FEED);
    }

    /** 引き戻し方向に逆転させる。 */
    public void retract() {
        feederMotor.setPower(Const.Feeder.Power.RETRACT);
    }

    /** フィーダーを停止する。 */
    public void stop() {
        feederMotor.setPower(Const.Feeder.Power.STOP);
    }
}
