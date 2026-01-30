package org.firstinspires.ftc.teamcode.config;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * ロボットの設定値を一元管理するクラス。
 * サブシステムごとに階層化された構造。
 */
@Configurable
public final class Const {

    // ========== ドライブサブシステム ==========
    public static final class Drive {

        // --- モーター設定 ---
        public static final class Motor {
            public static final String LEFT_FRONT = "leftFront";
            public static final String RIGHT_FRONT = "rightFront";
            public static final String LEFT_REAR = "leftRear";
            public static final String RIGHT_REAR = "rightRear";
        }

        // --- モーター方向設定 ---
        public static final class Direction {
            public static final DcMotor.Direction LEFT_FRONT = DcMotor.Direction.REVERSE;
            public static final DcMotor.Direction RIGHT_FRONT = DcMotor.Direction.FORWARD;
            public static final DcMotor.Direction LEFT_REAR = DcMotor.Direction.REVERSE;
            public static final DcMotor.Direction RIGHT_REAR = DcMotor.Direction.FORWARD;
        }
    }

    // ========== シューターサブシステム ==========
    public static final class Shooter {

        public static double rpmFromDistance(double distance) {
            if (distance < 50) {
                    return Const.Shooter.Velocity.LOWEST_RPM;
            } else if (distance < 80) {
                    return Const.Shooter.Velocity.NORMAL_RPM;
            } else if (distance < 110) {
                    return Const.Shooter.Velocity.MEDIUM_HIGH_RPM;
            } else {
                    return Const.Shooter.Velocity.HIGHEST_RPM;
            }
        }


        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "ShooterMotor";
        }

        public static final class PID {
            public static final double KP = 0.0073;
            public static final double KI = 0.0;
            public static final double KD = 0;
        }

        // --- パワー設定 ---
        public static final class Velocity {
            public static double HIGHEST_RPM = 1600;
            public static double MEDIUM_HIGH_RPM = 1350;
            public static double NORMAL_RPM = 1300;
            public static double LOWEST_RPM = 1000;
            public static double REVERSE_TARGET_RPM = -1400;
            public static double TOLERANCE = 100;
        }

        // --- 制御設定 ---

    }

    // ========== インテークサブシステム ==========
    public static final class Intake {

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "IntakeMotor";
        }

        // --- パワー設定 ---
        public static final class Power {
            public static final double INTAKE = 1.0;
            public static final double STOP = 0.0;
            public static final double REVERSE = -1.0;
        }
    }

    // ========== フィーダーサブシステム ==========
    public static final class Feeder {

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "FeederMotor";
            public static final double isAttime = 0.15;
        }

        // --- パワー設定 ---
        public static final class Power {
            public static final double FEED = 1.0;
            public static final double WEAKFEED = 0.3;
            public static final double RETRACT = -1.0;
        }
    }

    public static final class Imu {
        public static final String NAME = "imu";
        public static final RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIRECTION = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        public static final RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIRECTION = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
    }
}
