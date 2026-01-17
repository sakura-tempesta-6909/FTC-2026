package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.config.PIDTuning;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(PIDTuning.KP, PIDTuning.KI, PIDTuning.KD);
    private ControlSystem controller;

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
        double power = controller.calculate(new KineticState(shooterMotor.getCurrentPosition(), shooterMotor.getVelocity()));
        shooterMotor.setPower(power);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("P", pidCoefficients.kP);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("power", power);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("isAtVelocity", isAtVelocity());
    }

    public void setTargetRPM(double rpm) {
        controller.setGoal(new KineticState(0.0, rpm));
    }

    public void setReverseTargetRPM(double rpm) {
        controller.setGoal(new KineticState(0.0, rpm));
    }

    public void stopShooter() {
        shooterMotor.setPower(0.0);
    }


    public boolean isAtVelocity() {
        double error = Math.abs(
                controller.getGoal().getVelocity()
                        - shooterMotor.getVelocity()
        );
        return error < 200;
    }

}
