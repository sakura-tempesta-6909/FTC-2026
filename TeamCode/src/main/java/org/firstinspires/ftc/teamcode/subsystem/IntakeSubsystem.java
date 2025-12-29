package org.firstinspires.ftc.teamcode.subsystem;

import static org.firstinspires.ftc.teamcode.config.Const.Intake.INTAKE_POWER;
import static org.firstinspires.ftc.teamcode.config.Const.Intake.INTAKE_REVERSE_POWER;

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
    public double power = INTAKE_POWER;
    public double reverse_power = INTAKE_REVERSE_POWER;

    private MotorEx IntakeMotor;

    @Override
    public void initialize() {
        IntakeMotor = new MotorEx(Const.Motor.INTAKE);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void periodic() {
        IntakeMotor.setPower(power);
    }

    public final Command intake() {
         return new LambdaCommand()
                 .setStart(() -> IntakeMotor.setPower(power))
                 .setStop(interrupted -> IntakeMotor.setPower(0.0))
                 .setIsDone(() -> false)
                 .requires(this)
                 .named("stopIntake");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> IntakeMotor.setPower(0.0))
                .setIsDone(() -> true)
                .requires(this)
                .named("stopIntake");
    }

    public final Command outtake() {
        return new LambdaCommand()
                .setStart(() -> IntakeMotor.setPower(reverse_power))
                .setStop(interrupted -> IntakeMotor.setPower(0.0))
                .setIsDone(() -> false)
                .requires(this)
                .named("outtakeArtifacts");
    }



}
