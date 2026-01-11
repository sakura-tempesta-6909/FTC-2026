package org.firstinspires.ftc.teamcode.lib.pedroPathing;

import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.DriveEncoderConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.config.RobotConfig;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(2)
            .forwardZeroPowerAcceleration(-90.278)
            .lateralZeroPowerAcceleration(-203.5)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0.0,0.01,0.03))
            .headingPIDFCoefficients(new PIDFCoefficients(8 , 0, 0.01, 0.02))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.03,0.0,0.00001,0.6,0.01))
            .centripetalScaling(0.005);
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(RobotConfig.DriveMotor.RIGHT_FRONT)
            .rightRearMotorName(RobotConfig.DriveMotor.RIGHT_REAR)
            .leftRearMotorName(RobotConfig.DriveMotor.LEFT_REAR)
            .leftFrontMotorName(RobotConfig.DriveMotor.LEFT_FRONT)
            .leftFrontMotorDirection(RobotConfig.DriveMotor.LEFT_FRONT_DIR)
            .leftRearMotorDirection(RobotConfig.DriveMotor.LEFT_REAR_DIR)
            .rightFrontMotorDirection(RobotConfig.DriveMotor.RIGHT_FRONT_DIR)
            .rightRearMotorDirection(RobotConfig.DriveMotor.RIGHT_REAR_DIR)
            .xVelocity(83.495)
            .yVelocity(84.8075)
            ;

    public static DriveEncoderConstants localizerConstants = new DriveEncoderConstants()
            .rightFrontMotorName(RobotConfig.DriveMotor.RIGHT_FRONT)
            .rightRearMotorName(RobotConfig.DriveMotor.RIGHT_REAR)
            .leftRearMotorName(RobotConfig.DriveMotor.LEFT_REAR)
            .leftFrontMotorName(RobotConfig.DriveMotor.LEFT_FRONT)
            .leftFrontEncoderDirection(Encoder.REVERSE)
            .leftRearEncoderDirection(Encoder.REVERSE)
            .rightFrontEncoderDirection(Encoder.FORWARD)
            .rightRearEncoderDirection(Encoder.FORWARD)
            .robotWidth(26*0.393701)
            .robotLength(32*0.393701)
            .forwardTicksToInches(0.00794)
            .strafeTicksToInches(0.00875)
            .turnTicksToInches(0.0152)
            ;

//    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
//            .forwardEncoder_HardwareMapName(RobotConfig.DriveMotor.RIGHT_REAR)
//            .strafeEncoder_HardwareMapName(RobotConfig.DriveMotor.RIGHT_FRONT)
//            .IMU_HardwareMapName(RobotConfig.Imu.NAME)
//            .IMU_Orientation(RobotConfig.Imu.ORIENTATION)
//            .strafePodX(-17 * 0.393701)
//            .forwardPodY(0)
//            .strafeEncoderDirection(Encoder.FORWARD)
//            .forwardEncoderDirection(Encoder.REVERSE)
//            .forwardTicksToInches(5.205)
//            .strafeTicksToInches(40/155)
//            ;

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .driveEncoderLocalizer(localizerConstants)
//                .twoWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
