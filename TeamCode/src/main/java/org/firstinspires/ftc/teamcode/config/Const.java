package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.robotcore.hardware.DcMotor;

/**
 * ロボットの設定値を一元管理するクラス。
 * サブシステムごとに階層化された構造。
 */
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
            public static final DcMotor.Direction LEFT_FRONT = DcMotor.Direction.FORWARD;
            public static final DcMotor.Direction RIGHT_FRONT = DcMotor.Direction.REVERSE;
            public static final DcMotor.Direction LEFT_REAR = DcMotor.Direction.FORWARD;
            public static final DcMotor.Direction RIGHT_REAR = DcMotor.Direction.REVERSE;
        }
    }

    // ========== シューターサブシステム ==========
    public static final class Shooter {

        // --- モーター設定 ---
        public static final class Motor {
            public static final String NAME = "ShooterMotor";
        }

        public static final class PID {
            public static final double KP = 0.00035;
            public static final double KI = 0.00000035;
            public static final double KD = 0;
        }

        // --- パワー設定 ---
        public static final class Velocity {
            public static final double TARGET_RPM = 1800;
            public static final double REVERSE_TARGET_RPM = -1800;
            public static final double MIN_SHOOT_RPM = 1700;
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
        }

        // --- パワー設定 ---
        public static final class Power {
            public static final double FEED = 1.0;
            public static final double RETRACT = -1.0;
        }
    }
}
