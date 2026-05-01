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

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "ShooterMotor";
        }

        // --- PID 係数 ---
        public static final class PID {
            public static final double KP = 0.00743;
            public static final double KI = 0.000000000002;
            public static final double KD = 0.0;
        }

        // --- 速度 (RPM) ---
        public static final class Velocity {
            public static double HIGHEST_RPM = 1520;
            public static double MEDIUM_HIGH_RPM = 1400;
            public static double NORMAL_RPM = 1280;
            public static double MEDIUM_LOW_RPM = 1200;
            public static double RPM_FOR_NEAR = 1150;
            public static double REVERSE_TARGET_RPM = -1400;
            /**
             * HOLD 状態で PID に与える目標 RPM。微小な負値で前方向への自然回転を阻止する。
             */
            public static double HOLD_RPM = -50;
            public static final double STOP = 0.0;
            public static double TOLERANCE = 100;
        }

        // --- 距離レンジ閾値 (cm) ---
        public static final class DistanceThreshold {
            public static double SHORT = 60;
            public static double MEDIUM_SHORT = 80;
            public static double MEDIUM = 110;
            public static double LONG = 130;
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
            /**
             * 射撃中にボールが外に飛び出さないようゆっくり回すためのパワー。
             */
            public static double SLOW_INTAKE = 0.3;
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
            public static final double FEED = 0.7;
            public static final double WEAK_FEED = 0.22;
            public static final double RETRACT = -1.0;
            public static final double STOP = 0.0;
        }
    }

    // ========== 射出ルーチン共通設定 ==========
    public static final class ShootingRoutine {
        /** 射出前にフィーダーを引き戻す時間 (秒)。 */
        public static double RETRACT_DURATION_SECONDS = 0.04;
    }

    // ========== Limelight ==========
    public static final class Limelight {
        public static final String DEVICE_NAME = "limelight";
        public static final int PIPELINE = 0;

        /**
         * Ta (タグ占有面積%) → 距離の較正定数。
         * <pre>
         *   distanceToTag = SCALE * Ta^EXPONENT       (cm)
         *   distance      = sqrt(distanceToTag^2 - HEIGHT_OFFSET_SQUARED)
         * </pre>
         */
        public static final class DistanceCalibration {
            public static double SCALE = 196.1;
            public static double EXPONENT = -0.8030557;
            /** カメラとタグの高さ差の二乗 (cm^2)。 */
            public static double HEIGHT_OFFSET_SQUARED = 1980.25;
        }

        /** Limelight botpose (メートル, フィールド中心) → Pedro 座標系 (インチ, フィールド角) への変換。 */
        public static final class CoordinateConversion {
            public static final double METERS_TO_INCHES = 39.3701;
            public static final double FIELD_OFFSET_INCHES = 72;
        }
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
