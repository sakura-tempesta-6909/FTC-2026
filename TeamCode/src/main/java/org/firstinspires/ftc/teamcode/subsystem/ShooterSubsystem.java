package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.util.TrapezoidInterpolator;

public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;

    private final TrapezoidInterpolator powerProfile =
            new TrapezoidInterpolator(Const.Shooter.createProfile(), 0.0);

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Motor.SHOOTER);
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        powerProfile.reset();
    }

    @Override
    public void periodic() {
        double power = powerProfile.getPosition();
        shooterMotor.setPower(power);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", powerProfile.getGoalPosition());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("power", power);
    }

    public final Command shoot() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(Const.Shooter.SHOOT_POWER))
                .setIsDone(() -> false)
                .setStop(i -> powerProfile.setGoal(0.0))
                .requires(this)
                .named("shooterShoot");
    }

    public final Command reverse() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(Const.Shooter.REVERSE_POWER))
                .setIsDone(() -> false)
                .setStop(i -> powerProfile.setGoal(0.0))
                .requires(this)
                .named("shooterReverse");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(0.0))
                .setIsDone(() -> true)
                .requires(this)
                .named("shooterStop");
    }
    public boolean isAtVelocity() {
        double rpm = shooterMotor.getVelocity();
        return rpm >= Const.Shooter.MIN_SHOOT_RPM;
    }


}
