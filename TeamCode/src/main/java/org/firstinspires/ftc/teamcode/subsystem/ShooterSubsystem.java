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

@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(PIDTuning.KP, PIDTuning.KI, PIDTuning.KD);
    private ControlSystem controller;

    private double targetVelocity = 0.0;

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        controller.setGoal(new KineticState(0.0, 0.0));
    }

    @Override
    public void periodic() {
        if (targetVelocity == 0.0) {
            shooterMotor.setPower(0.0);
        } else {
            double power = controller.calculate(
                    new KineticState(0.0, shooterMotor.getVelocity())
            );
            shooterMotor.setPower(power);
        }

        PanelsTelemetry.INSTANCE.getTelemetry().addData("P", pidCoefficients.kP);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("isAtVelocity", isAtVelocity());
    }

    public void setTargetVelocity(double velocity) {
        this.targetVelocity = velocity;
        controller.setGoal(new KineticState(0.0, velocity));
    }

    public void setTargetRPM() {
        setTargetVelocity(Const.Shooter.Velocity.TARGET_RPM);
    }

    public void setReverseTargetRPM() {
        setTargetVelocity(Const.Shooter.Velocity.REVERSE_TARGET_RPM);
    }

    public void stop() {
        setTargetVelocity(0.0);
    }

    public double getVelocity() {
        return shooterMotor.getVelocity();
    }

    public boolean isAtVelocity() {
        double rpm = Math.abs(shooterMotor.getVelocity());
        return rpm >= Const.Shooter.Velocity.MIN_SHOOT_RPM;
    }
}
