package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.util.TrapezoidInterpolator;

public class IntakeSubsystem implements Subsystem {
    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx intakeMotor;

    private final TrapezoidInterpolator powerProfile =
            new TrapezoidInterpolator(Const.Intake.createProfile(), 0.0);

    @Override
    public void initialize() {
        intakeMotor = new MotorEx(Const.Motor.INTAKE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        powerProfile.reset();
    }

    @Override
    public void periodic() {
        double power = powerProfile.getPosition();
        intakeMotor.setPower(power);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", powerProfile.getGoalPosition());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("power", power);
    }

    public final Command intake() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(Const.Intake.INTAKE_POWER))
                .setIsDone(() -> false)
                .setStop(i -> powerProfile.setGoal(0.0))
                .requires(this)
                .named("intakeIntake");
    }

    public final Command outtake() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(Const.Intake.OUTTAKE_POWER))
                .setIsDone(() -> false)
                .setStop(i -> powerProfile.setGoal(0.0))
                .requires(this)
                .named("intakeOuttake");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> powerProfile.setGoal(0.0))
                .setIsDone(() -> true)
                .requires(this)
                .named("intakeStop");
    }

}
