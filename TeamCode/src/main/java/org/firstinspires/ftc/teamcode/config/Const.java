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

    private Const() {}

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

        /**
         * AprilTag までの距離 (cm) から目標 RPM を返す。
         * 距離レンジごとに段階的に切り替える。
         */
        public static double rpmFromDistance(double distance) {
            if (distance < DistanceThreshold.SHORT) {
                return Velocity.LOWEST_RPM;
            } else if (distance < DistanceThreshold.MEDIUM) {
                return Velocity.NORMAL_RPM;
            } else if (distance < DistanceThreshold.LONG) {
                return Velocity.MEDIUM_HIGH_RPM;
            } else {
                return Velocity.HIGHEST_RPM;
            }
        }

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "ShooterMotor";
        }

        // --- PID 係数 ---
        public static final class PID {
            public static final double KP = 0.0073;
            public static final double KI = 0.0;
            public static final double KD = 0.0;
        }

        // --- 速度 (RPM) ---
        public static final class Velocity {
            public static double HIGHEST_RPM = 1500;
            public static double MEDIUM_HIGH_RPM = 1400;
            public static double NORMAL_RPM = 1300;
            public static double LOWEST_RPM = 1100;
            public static double REVERSE_TARGET_RPM = -1400;
            public static final double STOP = 0.0;
            public static double TOLERANCE = 100;
        }

        // --- 距離レンジ閾値 (cm) ---
        public static final class DistanceThreshold {
            public static double SHORT = 50;
            public static double MEDIUM = 80;
            public static double LONG = 110;
        }

        /**
         * Limelight Ta (タグ占有面積%) から AprilTag までの距離を計算するための較正定数。
         * <pre>
         *   distanceToTag = SCALE * Ta^EXPONENT       (cm)
         *   distance      = sqrt(distanceToTag^2 - HEIGHT_OFFSET_SQUARED)
         * </pre>
         */
        public static final class DistanceCalibration {
            public static double SCALE = 196.1;
            public static double EXPONENT = -0.8030557;
            /** カメラとタグの高さ差の二乗 (cm^2)。Pythagorean 補正用。 */
            public static double HEIGHT_OFFSET_SQUARED = 4225;
        }
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
            public static final double REVERSE = -1.0;
            public static final double STOP = 0.0;
        }
    }

    // ========== フィーダーサブシステム ==========
    public static final class Feeder {

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "FeederMotor";
        }

        // --- パワー設定 ---
        public static final class Power {
            public static final double FEED = 1.0;
            public static final double WEAKFEED = 0.3;
            public static final double RETRACT = -1.0;
            public static final double STOP = 0.0;
        }
    }

    // ========== 射出ルーチン共通設定 ==========
    public static final class ShootingRoutine {
        /** 射出前にフィーダーを引き戻す時間 (秒)。 */
        public static double RETRACT_DURATION_SECONDS = 0.2;
    }

    // ========== IMU 設定 ==========
    public static final class Imu {
        public static final String NAME = "imu";
        public static final RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIRECTION =
                RevHubOrientationOnRobot.UsbFacingDirection.UP;
        public static final RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIRECTION =
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
    }
}
