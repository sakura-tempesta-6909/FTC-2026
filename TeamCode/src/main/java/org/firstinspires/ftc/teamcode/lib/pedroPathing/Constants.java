package org.firstinspires.ftc.teamcode.lib.pedroPathing;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.config.Const;

@Configurable
public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(8)
            .forwardZeroPowerAcceleration(-25.60869319770496)
            .lateralZeroPowerAcceleration(-49.0453151408264)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0, 0.02, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.08, 0.01))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.012, 0, 0.0006, 0.6, 0));
//            .useSecondaryTranslationalPIDF(true)
//            .useSecondaryHeadingPIDF(true)
//            .useSecondaryDrivePIDF(true)
//            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.2, 0, 0.01, 0))
//            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(1.5, 0, 0.01, 0.03))
//            .secondaryDrivePIDFCoefficients(new FilteredPIDFCoefficients(0.01, 0, 0, 0.6, 0))

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(Const.Drive.Motor.RIGHT_FRONT)
            .rightRearMotorName(Const.Drive.Motor.RIGHT_REAR)
            .leftRearMotorName(Const.Drive.Motor.LEFT_REAR)
            .leftFrontMotorName(Const.Drive.Motor.LEFT_FRONT)
            .leftFrontMotorDirection(Const.Drive.Direction.LEFT_FRONT)
            .leftRearMotorDirection(Const.Drive.Direction.LEFT_REAR)
            .rightFrontMotorDirection(Const.Drive.Direction.RIGHT_FRONT)
            .rightRearMotorDirection(Const.Drive.Direction.RIGHT_REAR)
            .xVelocity(69.02034)
            .yVelocity(59.059874746611854);

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName(Const.Drive.Motor.RIGHT_FRONT)
            .strafeEncoder_HardwareMapName(Const.Drive.Motor.RIGHT_REAR)
            .forwardPodY(0)
            .strafePodX(20 * 0.393701)
            .forwardTicksToInches(5.48710876667E-4)
            .strafeTicksToInches(5.353212522E-4)
            .IMU_HardwareMapName(Const.Imu.NAME)
            .IMU_Orientation(new RevHubOrientationOnRobot(Const.Imu.LOGO_FACING_DIRECTION, Const.Imu.USB_FACING_DIRECTION));

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .twoWheelLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}

