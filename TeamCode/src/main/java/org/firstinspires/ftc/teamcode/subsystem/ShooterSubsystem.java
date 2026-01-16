package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.config.Const;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(0.0004, 0.0000004, 0);
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


    public final Command shoot() {
        return new LambdaCommand().setStart(() -> controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.TARGET_RPM))).setIsDone(() -> false).setStop(i -> controller.setGoal(new KineticState(0.0, 0.0))).requires(this).named("shooterShoot");
    }

    public final Command reverse() {
        return new LambdaCommand().setStart(() -> controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.REVERSE_TARGET_RPM))).setIsDone(() -> false).setStop(i -> controller.setGoal(new KineticState(0.0, 0.0))).requires(this).named("shooterReverse");
    }

    public final Command stop() {
        return new LambdaCommand().setStart(() -> controller.setGoal(new KineticState(0.0, 0.0))).setIsDone(() -> true).requires(this).named("shooterStop");
    }

    public boolean isAtVelocity() {
        double rpm = Math.abs(shooterMotor.getVelocity());
        return rpm >= Const.Shooter.Control.MIN_SHOOT_RPM;
    }
}
