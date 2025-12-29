package org.firstinspires.ftc.teamcode.subsystem;

import com.qualcomm.robotcore.hardware.DcMotor;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.teamcode.config.Const;
public class IntakeSubsystem implements Subsystem {

    public static final IntakeSubsystem INSTANCE = new IntakeSubsystem();

    private MotorEx IntakeMotor;

    @Override
    public void initialize() {
        IntakeMotor = new MotorEx(Const.Motor.INTAKE);
        IntakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }


    public final Command intake() {
         return new LambdaCommand()
                 .setStart(() -> IntakeMotor.setPower(Const.Intake.INTAKE_POWER))
                 .setIsDone(() -> false)
                 .requires(this)
                 .named("stopIntake");
    }

    public final Command stop() {
        return new LambdaCommand()
                .setStart(() -> IntakeMotor.setPower(Const.Intake.INTAKE_STOP))
                .setIsDone(() -> true)
                .requires(this)
                .named("stopIntake");
    }

    public final Command outtake() {
        return new LambdaCommand()
                .setStart(() -> IntakeMotor.setPower(Const.Intake.INTAKE_REVERSE_POWER))
                .setIsDone(() -> false)
                .requires(this)
                .named("outtakeArtifacts");
    }



}
