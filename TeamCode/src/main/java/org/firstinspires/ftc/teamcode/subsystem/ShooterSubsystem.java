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
    public enum ShooterState {
        setTargetRPM,
        setReverseTargetRPM,
        stopShooter
    }

    private ShooterState state = ShooterState.stopShooter;

    public void setState(ShooterState state) {
        this.state = state;
    }

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
        switch (state) {
            case setTargetRPM ->
                    controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.TARGET_RPM));

            case setReverseTargetRPM ->
                    controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.REVERSE_TARGET_RPM));

            case stopShooter -> {
                shooterMotor.setPower(0.0);
                controller.setGoal(new KineticState(0.0, 0.0));
                return;
            }
        }
        double power = controller.calculate(
                new KineticState(0.0, shooterMotor.getVelocity())
        );
        shooterMotor.setPower(power);

        PanelsTelemetry.INSTANCE.getTelemetry().addData("P", pidCoefficients.kP);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("isAtVelocity", isAtVelocity());
    }


    public boolean isAtVelocity() {
        double rpm = Math.abs(shooterMotor.getVelocity());
        return rpm >= Const.Shooter.Velocity.MIN_SHOOT_RPM;
    }

}
