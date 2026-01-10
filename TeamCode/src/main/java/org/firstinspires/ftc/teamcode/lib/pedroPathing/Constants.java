package org.firstinspires.ftc.teamcode.lib.pedroPathing;

import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.Encoder;
import com.pedropathing.ftc.localization.constants.TwoWheelConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.config.Const;

public class Constants {
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(3);
    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName(Const.Drive.Motor.Name.RIGHT_FRONT)
            .rightRearMotorName(Const.Drive.Motor.Name.RIGHT_REAR)
            .leftRearMotorName(Const.Drive.Motor.Name.LEFT_REAR)
            .leftFrontMotorName(Const.Drive.Motor.Name.LEFT_FRONT)
            .leftFrontMotorDirection(Const.Drive.Motor.Direction.LEFT_FRONT)
            .leftRearMotorDirection(Const.Drive.Motor.Direction.LEFT_REAR)
            .rightFrontMotorDirection(Const.Drive.Motor.Direction.RIGHT_FRONT)
            .rightRearMotorDirection(Const.Drive.Motor.Direction.RIGHT_REAR);

    public static TwoWheelConstants localizerConstants = new TwoWheelConstants()
            .forwardEncoder_HardwareMapName(Const.Drive.Motor.Name.RIGHT_REAR)
            .strafeEncoder_HardwareMapName(Const.Drive.Motor.Name.LEFT_REAR)
            .IMU_HardwareMapName(Const.Imu.NAME)
            .IMU_Orientation(new RevHubOrientationOnRobot(Const.Imu.logoFacingDirection, Const.Imu.usbFacingDirection))
            .strafePodX(20 * 0.393701)
            .forwardPodY(0)
            .strafeEncoderDirection(Encoder.REVERSE)
            .forwardEncoderDirection(Encoder.REVERSE);
//            .forwardTicksToInches(5.36426);
//            .strafeTicksToInches(40/155);

    public static PathConstraints pathConstraints = new PathConstraints(0.99, 100, 1, 1);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .twoWheelLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .mecanumDrivetrain(driveConstants)
                .build();
    }
}
