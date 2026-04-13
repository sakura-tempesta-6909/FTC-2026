package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * フライホイール式シューターを制御するサブシステム。
 * <p>
 * NextControl の {@link ControlSystem} で速度 PID を毎ループ走らせる。
 * 距離に基づく目標 RPM の決定には {@link LimelightSubsystem} から距離を取得する。
 */
@Configurable
public class ShooterSubsystem implements Subsystem {

    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    public static PIDCoefficients pidCoefficients =
            new PIDCoefficients(Const.Shooter.PID.KP, Const.Shooter.PID.KI, Const.Shooter.PID.KD);

    private MotorEx shooterMotor;
    private ControlSystem controller;

    private ShooterSubsystem() {}

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.brakeMode();
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.STOP));
    }

    @Override
    public void periodic() {
        double power;
        if (controller.getGoal().getVelocity() == 0) {
            // 停止時はモーターパワーを直接 0 にして、ボールが押し出されるのを防ぐ
            power = 0;
        } else {
            power = controller.calculate(
                    new KineticState(0.0, shooterMotor.getVelocity()));
        }
        shooterMotor.setPower(power);

        // テレメトリ (update() は Main.onUpdate で一括送信)
        var telemetry = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry.addData("[射出] 状態", getStatusText());
        telemetry.addData("[射出] RPM", String.format("%.0f / %.0f", getCurrentVelocity(), getTargetVelocity()));
    }

    // --- 目標速度 API ---

    public void setTargetVelocity(double velocity) {
        controller.setGoal(new KineticState(0.0, velocity));
    }

    /** {@link LimelightSubsystem} の距離から段階的に目標 RPM を決めてセットする。 */
    public void setTargetRPM() {
        double distance = LimelightSubsystem.INSTANCE.getDistance();
        setTargetVelocity(rpmFromDistance(distance));
    }

    /** 距離 (cm) に応じた目標 RPM を返す。段階的に切り替える。 */
    private static double rpmFromDistance(double distance) {
        if (distance < Const.Shooter.DistanceThreshold.SHORT) {
            return Const.Shooter.Velocity.LOWEST_RPM;
        } else if (distance < Const.Shooter.DistanceThreshold.MEDIUM) {
            return Const.Shooter.Velocity.NORMAL_RPM;
        } else if (distance < Const.Shooter.DistanceThreshold.LONG) {
            return Const.Shooter.Velocity.MEDIUM_HIGH_RPM;
        } else {
            return Const.Shooter.Velocity.HIGHEST_RPM;
        }
    }

    public void setReverseTargetRPM() {
        setTargetVelocity(Const.Shooter.Velocity.REVERSE_TARGET_RPM);
    }

    public void stop() {
        setTargetVelocity(Const.Shooter.Velocity.STOP);
    }

    public boolean isAtVelocity() {
        return Math.abs(shooterMotor.getVelocity() - controller.getGoal().getVelocity())
                <= Const.Shooter.Velocity.TOLERANCE;
    }

    /** 目標 RPM。停止中は 0。 */
    public double getTargetVelocity() {
        return controller.getGoal().getVelocity();
    }

    /** 現在の実 RPM。 */
    public double getCurrentVelocity() {
        return shooterMotor.getVelocity();
    }

    /**
     * Driver Hub 向けの状態テキストを返す。
     * "停止" / "加速中 (85%)" / "準備完了"
     */
    public String getStatusText() {
        double target = getTargetVelocity();
        if (target == 0) return "停止";
        if (isAtVelocity()) return "準備完了";
        return String.format("加速中 (%.0f%%)", Math.min(getCurrentVelocity() / target * 100, 100));
    }
}
