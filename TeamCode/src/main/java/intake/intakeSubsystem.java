package intake;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import dev.nextftc.core.subsystems.Subsystem;
public class intakeSubsystem {
    private DcMotor intakeMotor;

    public intakeSubsystem(HardwareMap hardwareMap) {
        intakeMotor = hardwareMap.get(DcMotor.class, "intakemotor");
    }

    public void intakeIn() {
        intakeMotor.setPower(1.0);
    }

    public void stop() {
        intakeMotor.setPower(0.0);
    }
}




