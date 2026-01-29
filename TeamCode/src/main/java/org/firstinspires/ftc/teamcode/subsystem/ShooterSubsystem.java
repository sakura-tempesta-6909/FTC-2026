package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.config.PIDTuning;


@Configurable
public class ShooterSubsystem implements Subsystem {
    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    private MotorEx shooterMotor;
    public static PIDCoefficients pidCoefficients = new PIDCoefficients(PIDTuning.KP, PIDTuning.KI, PIDTuning.KD);
    private ControlSystem controller;

    private double targetVelocity = 0.0;
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private Limelight3A limelight;
    private IMU imu;
    private double distanceToTag;
    private double distance;
    private double a;
    private double b;
    private int lastSnapshotSize = 0;
    private Telemetry telemetry;

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.brakeMode();
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        targetVelocity = 0.0;
        controller.setGoal(new KineticState(0.0, 0.0));
        telemetry = panelsTelemetry.getFtcTelemetry();
        limelight = ActiveOpMode.hardwareMap().get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        imu = ActiveOpMode.hardwareMap().get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
    }

    @Override
    public void periodic() {
        double power = controller.calculate(
                new KineticState(0.0, shooterMotor.getVelocity())
        );
        shooterMotor.setPower(power);
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null && llResult.isValid()) {
            Pose3D botpose = llResult.getBotpose_MT2();
            distanceToTag = getDistanceToTag(llResult.getTa());
            distance = getDistance(distanceToTag);
            telemetry.addData("Distance", distance);
            telemetry.addData("DistanceToTag", distanceToTag);
            telemetry.addData("Tx", llResult.getTx());
            telemetry.addData("Ty", llResult.getTy());
            telemetry.addData("Ta", llResult.getTa());
        }
        PanelsTelemetry.INSTANCE.getTelemetry().addData("P", pidCoefficients.kP);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("goal", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("isAtVelocity", isAtVelocity());
        panelsTelemetry.getTelemetry().update();
    }

    public void setTargetVelocity(double velocity) {
        this.targetVelocity = velocity;
        controller.setGoal(new KineticState(0.0, velocity));
    }

    public void setTargetRPM() {
        if (distance < 50) {
            setTargetVelocity(Const.Shooter.Velocity.LOWEST_RPM);
        } else if (distance < 80) {
            setTargetVelocity(Const.Shooter.Velocity.NORMAL_RPM);
        } else if (distance < 110) {
            setTargetVelocity(Const.Shooter.Velocity.MEDIUM_HIGH_RPM);
        } else {
            setTargetVelocity(Const.Shooter.Velocity.HIGHEST_RPM);
        }
    }

    public void setRPMFromDistance(double distance) {
        double rpm = Const.Shooter.rpmFromDistance(distance);
        setTargetVelocity(rpm);
    }

    public void setHighestRPM() {
        setTargetVelocity(Const.Shooter.Velocity.HIGHEST_RPM);
    }

    public void setMediumHighRPM() {
        setTargetVelocity(Const.Shooter.Velocity.MEDIUM_HIGH_RPM);
    }

    public void setNormalRPM() {
        setTargetVelocity(Const.Shooter.Velocity.NORMAL_RPM);
    }

    public void setLowestRPM() {
        setTargetVelocity(Const.Shooter.Velocity.LOWEST_RPM);
    }


    public void setReverseTargetRPM() {
        setTargetVelocity(Const.Shooter.Velocity.REVERSE_TARGET_RPM);
    }

    public void stop() {
        setTargetVelocity(0.0);
    }


    public boolean isAtVelocity() {
        return Math.abs(shooterMotor.getVelocity() - controller.getGoal().getVelocity()) <= Const.Shooter.Velocity.TOLERANCE;
    }

    public double getDistanceToTag(double Ta) {
        double scale = 196.1;
        double b = Math.pow(Ta, -0.8030557);
        double distanceToTag = scale * b;
        return distanceToTag;
    }

    public double getDistance(double distanceToTag) {
        a = Math.pow(distanceToTag, 2);
        b = a - 4225;
        distance = Math.pow(b, 0.5);
        return distance;
    }
}
