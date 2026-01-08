package org.firstinspires.ftc.teamcode.config;

import dev.nextftc.control.KineticState;

/**
 * ロボットの設定値を一元管理するクラス。
 */
public final class Const {

    private Const() {}

    // ========== モーター名 ==========
    public static final class Motor {
        public static final String INTAKE = "IntakeMotor";
        public static final String SHOOTER = "ShooterMotor";
        public static final String FEEDER = "FeederMotor";
    }

    // ========== シューター設定 ==========
    public static final class Shooter {
        public static final double TARGET_VELOCITY = 1800;
        public static final double REVERSE_VELOCITY = -1800;
        public static final double STOPPED_VELOCITY = 0;

        public static final double kP = 0.00025;
        public static final double kI = 0.0000004;
        public static final double kD = 0.0;
        public static final double SHOOTER_VELOCITY_TOLERANCE = 100;
        public static final double kF = 1.0;
        public static final double I_MAX = 0.20;
        public static final double MIN_POWER = -1.0;
        public static final double MAX_POWER = 1.0;

    }


    public static final class Intake {

        public static final double INTAKE_POWER = 1.0;
        public static final double INTAKE_STOP = 0.0;
        public static final double INTAKE_REVERSE_POWER = -1.0;
    }

    // ========== フィーダー設定 ==========
    public static final class Feeder {
        public static final double FEED_POWER = 1.0;
        public static final double RETRACT_POWER = -1.0;
    }
}
