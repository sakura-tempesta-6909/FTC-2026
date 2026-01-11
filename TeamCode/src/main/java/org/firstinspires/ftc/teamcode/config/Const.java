package org.firstinspires.ftc.teamcode.config;

import dev.nextftc.control.KineticState;
import org.firstinspires.ftc.teamcode.util.TrapezoidInterpolator;
import org.firstinspires.ftc.teamcode.util.TrapezoidParameters;

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
            public static final double SHOOT_POWER = 0.8;
            public static final double REVERSE_POWER = -0.8;

            // 台形プロファイル
            public static final double MAX_VEL = 2.0;      // [power/sec]
            public static final double MAX_ACCEL = 10.0;   // [power/sec^2]
            public static final double MAX_DT = 0.05;      // [sec]
            public static final double MIN_POWER = -1.0;
            public static final double MAX_POWER = 1.0;
            // Const.Shooter
            public static final double MIN_SHOOT_RPM = 1800;


            public static TrapezoidParameters createProfile() {
                return new TrapezoidParameters(MAX_VEL, MAX_ACCEL, MAX_DT, MIN_POWER, MAX_POWER);
            }
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