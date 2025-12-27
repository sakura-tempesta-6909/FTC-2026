package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import dev.nextftc.control.KineticState;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.util.TrapezoidInterpolator;
import org.firstinspires.ftc.teamcode.util.TrapezoidParameters;


public class IntakeSubsystem implements Subsystem {
    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx motor;

    private final TrapezoidInterpolator ramp =
            new TrapezoidInterpolator(new TrapezoidParameters(
                    2.0,   // maxVel (power/sec)
                    10.0,  // maxAccel (power/sec^2)
                    0.05   // maxDt
            ), 0.0);

    private double lastPower = 0.0;

    @Override
    public void initialize() {
        motor = new MotorEx("IntakeMotor");
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        setTarget(0.0);
        ramp.reset();
        motor.setPower(0.0);
    }

    @Override
    public void periodic() {
        double p = Range.clip(ramp.getCurrentReference().getPosition(), -1.0, 1.0);
        motor.setPower(p);
        lastPower = p;
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Intake goal", ramp.getGoal().getPosition());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Intake power", p);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Intake currnt", motor.getPower());
    }

    public void setTarget(double power) {
        ramp.setGoal(new KineticState(Range.clip(power, -1.0, 1.0), 0.0, 0.0));
    }

    public final Command intake() {
        return new LambdaCommand()
                .setStart(() -> setTarget(1.0))
                .setIsDone(() -> false)
                .setStop(i -> setTarget(0.0))
                .requires(this)
                .named("intakeIntake");
    }

    public final Command outtake() {
        return new LambdaCommand()
                .setStart(() -> setTarget(-1.0))
                .setIsDone(() -> false)
                .setStop(i -> setTarget(0.0))
                .requires(this)
                .named("intakeOuttake");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> setTarget(0.0))
                .setIsDone(() -> true)
                .requires(this)
                .named("intakeStop");
    }

}
