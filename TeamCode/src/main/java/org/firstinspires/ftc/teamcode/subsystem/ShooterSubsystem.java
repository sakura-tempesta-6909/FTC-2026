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
import org.firstinspires.ftc.teamcode.config.PIDTuning;

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
            new PIDCoefficients(PIDTuning.KP, PIDTuning.KI, PIDTuning.KD);

    private MotorEx shooterMotor;
    private ControlSystem controller;

    private ShooterSubsystem() {}

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.brakeMode();
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.STOP));
    }

    @Override
    public void periodic() {
        double power = controller.calculate(
                new KineticState(0.0, shooterMotor.getVelocity()));
        shooterMotor.setPower(power);

        // テレメトリ (update() は Main.onUpdate で一括送信)
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter target", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter atVelocity", isAtVelocity());
    }

    // --- 目標速度 API ---

    public void setTargetVelocity(double velocity) {
        controller.setGoal(new KineticState(0.0, velocity));
    }

    /** {@link LimelightSubsystem} の距離から段階的に目標 RPM を決めてセットする。 */
    public void setTargetRPM() {
        double distance = LimelightSubsystem.INSTANCE.getDistance();
        setTargetVelocity(Const.Shooter.rpmFromDistance(distance));
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
}
