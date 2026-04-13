package org.firstinspires.ftc.teamcode.lib.pedroPathing;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.config.Const;

@Configurable
public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(11.6)
//            .mass(8.5)
            .forwardZeroPowerAcceleration(-31.51633782361255)
            .lateralZeroPowerAcceleration(-68.90771663346152)
            .translationalPIDFCoefficients(new PIDFCoefficients(0.1, 0.0001, 0.01, 0))
            .useSecondaryTranslationalPIDF(true)
            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(0.06, 0.00015, 0.02, 0))
            .headingPIDFCoefficients(new PIDFCoefficients(0.8, 0.00002, 0.02, 0))
            .drivePIDFCoefficients(new FilteredPIDFCoefficients(0.18, 0, 0.015, 0.6, 0.01))
            .useSecondaryHeadingPIDF(true)
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(0.5, 0.00002, 0.02, 0))
            .centripetalScaling(0.0007);

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
            .xVelocity(77.33926703986221)
            .yVelocity(60.2571713905635);

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName(Const.Drive.Motor.RIGHT_FRONT)
            .strafeEncoder_HardwareMapName(Const.Drive.Motor.RIGHT_REAR)
            .forwardPodY(0)
            .strafePodX(-17 * 0.393701)
            .forwardTicksToInches(5.48710876667E-4)
            .strafeTicksToInches(5.353212522E-4)
            .IMU_HardwareMapName(Const.Imu.NAME)
            .IMU_Orientation(new RevHubOrientationOnRobot(Const.Imu.LOGO_FACING_DIRECTION, Const.Imu.USB_FACING_DIRECTION));

    public static PinpointConstants pinPointLocalizerConstants = new PinpointConstants()
            .forwardPodY(0)
            .strafePodX(-13.5)
            .distanceUnit(DistanceUnit.CM)
            .hardwareMapName("pinpoint")
            .encoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1.2, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
//                .twoWheelLocalizer(localizerConstants)
                .pinpointLocalizer(pinPointLocalizerConstants)
                .mecanumDrivetrain(driveConstants)
                .pathConstraints(pathConstraints)
                .build();
    }
}