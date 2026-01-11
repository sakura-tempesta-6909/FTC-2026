package org.firstinspires.ftc.teamcode.config;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.teamcode.util.TrapezoidParameters;

/**
 * ロボットの設定値を一元管理するクラス。
 */
public final class Const {

    private Const() {

    }

    // ========== モーター名 ==========
    public static final class Motor {
        public static final String SHOOTER = "ShooterMotor";
        public static final String FEEDER = "FeederMotor";
        public static final String INTAKE = "IntakeMotor";
    }

    // ========== シューター設定 ==========
    public static final class Shooter {
        public static final double SHOOT_POWER = 0.5;
        public static final double REVERSE_POWER = -0.5;

        // 台形プロファイル
        public static final double MAX_VEL = 2.0;      // [power/sec]
        public static final double MAX_ACCEL = 10.0;   // [power/sec^2]
        public static final double MAX_DT = 0.05;      // [sec]
        public static final double MIN_POWER = -1.0;
        public static final double MAX_POWER = 1.0;
        // Const.Shooter
        public static final double MIN_SHOOT_RPM = 1900;


        public static TrapezoidParameters createProfile() {
            return new TrapezoidParameters(MAX_VEL, MAX_ACCEL, MAX_DT, MIN_POWER, MAX_POWER);
        }
    }

    // ========== フィーダー設定 ==========
    public static final class Feeder {
        public static final double FEED_POWER = 0.3;
        public static final double RETRACT_POWER = -0.3;
    }

    public static final class Drive {
        public static final class Motor {
            public static final class Name {
                public static final String LEFT_FRONT = "leftFront";
                public static final String LEFT_REAR = "leftRear";
                public static final String RIGHT_FRONT = "rightFront";
                public static final String RIGHT_REAR = "rightRear";
            }

            public static final class Direction {
                public static final DcMotorSimple.Direction LEFT_FRONT = DcMotorSimple.Direction.FORWARD;
                public static final DcMotorSimple.Direction LEFT_REAR = DcMotorSimple.Direction.FORWARD;
                public static final DcMotorSimple.Direction RIGHT_FRONT = DcMotorSimple.Direction.REVERSE;
                public static final DcMotorSimple.Direction RIGHT_REAR = DcMotorSimple.Direction.REVERSE;
            }
        }
    }

    public static final class Intake {
        public static final double INTAKE_POWER = 1.0;

        public static final double RETRACT_POWER = -1.0;
    }

    public static final class Imu {
        public static final String NAME = "imu";
        public static final RevHubOrientationOnRobot.LogoFacingDirection logoFacingDirection = RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
        public static final RevHubOrientationOnRobot.UsbFacingDirection usbFacingDirection = RevHubOrientationOnRobot.UsbFacingDirection.DOWN;
    }
}
