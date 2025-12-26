package intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import dev.nextftc.core.subsystems.Subsystem;
public class intakeSubsystem implements Subsystem {
    private final DcMotor intakeMotor;
    private double targetPower = 0.0;


    public intakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "IntakeMotor");
    }
    @Override
    public void initialize() {
        intakeMotor.setPower(0.0);
    }
    public void setPower(double power) {
        targetPower = power;
    }
    @Override
    public void periodic() {
        System.out.println("PERIODIC RUNNING");
        intakeMotor.setPower(targetPower);
    }
    public void stop() {
        intakeMotor.setPower(0.0);
    }
}




