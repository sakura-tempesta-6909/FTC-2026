package org.firstinspires.ftc.teamcode.config;

import org.firstinspires.ftc.teamcode.util.TrapezoidParameters;

/**
 * ロボットの設定値を一元管理するクラス。
 */
public final class Const {

    private Const() {
    }

    // ========== モーター名 ==========
    public static final class Motor {
        public static final String INTAKE = "IntakeMotor";
        public static final String FEEDER = "FeederMotor";
    }

    // ========== インテーク設定 ==========
    public static final class Intake {
        public static final double INTAKE_POWER = 1.0;
        public static final double OUTTAKE_POWER = -1.0;

        // 台形プロファイル
        public static final double MAX_VEL = 2.0;      // [power/sec]
        public static final double MAX_ACCEL = 10.0;   // [power/sec^2]
        public static final double MAX_DT = 0.05;      // [sec]
        public static final double MIN_POWER = -1.0;
        public static final double MAX_POWER = 1.0;

        public static TrapezoidParameters createProfile() {
            return new TrapezoidParameters(MAX_VEL, MAX_ACCEL, MAX_DT, MIN_POWER, MAX_POWER);
        }
    }

    // ========== フィーダー設定 ==========
    public static final class Feeder {
        public static final double FEED_POWER = 0.3;
        public static final double RETRACT_POWER = -0.3;
    }
}
