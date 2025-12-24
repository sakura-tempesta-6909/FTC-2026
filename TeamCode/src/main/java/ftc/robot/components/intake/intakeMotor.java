package ftc.robot.components.intake;

import com.qualcomm.robotcore.hardware.DcMotor;

public class intakeMotor extends intakebase{
    private final DcMotor intakeMotor;


    public intakeMotor(DcMotor motor){
        this.intakeMotor = motor;


    }

    @Override
    public void start(){

        intakeMotor.setPower(1.0);
    }
    @Override
    public void stop(){
        intakeMotor.setPower(0.0);
    }
}
