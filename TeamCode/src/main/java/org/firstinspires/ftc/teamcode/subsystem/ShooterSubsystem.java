package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;

import org.firstinspires.ftc.teamcode.config.Const;

@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;

    public static PIDCoefficients pidCoefficients = new PIDCoefficients(Const.Shooter.kP, Const.Shooter.kI, Const.Shooter.kD);
    private ControlSystem controller;

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Motor.SHOOTER);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterMotor.reverse();
        controller = ControlSystem.builder()
                .velPid(pidCoefficients)
                .build();
        controller.setGoal(new KineticState(0.0, 0.0));
    }

    @Override
    public void periodic() {
        shooterMotor.setPower(controller.calculate());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("power", controller.calculate());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("isGoalReached", isGoalReached());
    }



    public final Command shoot() {
        return new LambdaCommand()
                .setStart(() -> controller.setGoal(new KineticState(0.0,Const.Shooter.TARGET_VELOCITY)))
                .setIsDone(() -> false)
                .setStop(i -> controller.setGoal(new KineticState(0.0,Const.Shooter.STOPPED_VELOCITY)))
                .requires(this)
                .named("shooterShoot");
    }

    public final Command reverse() {
        return new LambdaCommand()
                .setStart(() -> controller.setGoal(new KineticState(0.0,Const.Shooter.REVERSE_VELOCITY)))
                .setIsDone(() -> false)
                .setStop(i -> controller.setGoal(new KineticState(0.0,Const.Shooter.STOPPED_VELOCITY)))
                .requires(this)
                .named("shooterReverse");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> controller.setGoal(new KineticState(0.0,Const.Shooter.STOPPED_VELOCITY)))
                .setIsDone(() -> true)
                .requires(this)
                .named("shooterStop");
    }
    public boolean isGoalReached() {
        return controller.isWithinTolerance(new KineticState(0.0,Const.Shooter.SHOOTER_VELOCITY_TOLERANCE));
    }

}
