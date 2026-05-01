package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

import java.util.Locale;

/**
 * シンプルな手動操縦 OpMode。
 * <ul>
 *   <li>メカナムドライブ: gamepad1 左スティック (移動) / 右スティック X (旋回)
 *   <li>Shooter: Start 後 PID で目標 RPM 維持
 *   <li>Intake: Start 直後 ON、leftBumper = ON / rightBumper = OFF で切替
 *   <li>Shoot: gamepad1.a ホールド中は feeder を全力で回して発射
 *   <li>Nudge: gamepad1.b で feeder を短時間ゆっくり回す (装填調整)
 *   <li>Outtake: gamepad1.y ホールドで shooter 弱逆回転 + feeder/intake 全力逆回転 (詰まり解消)
 * </ul>
 */
@TeleOp(name = "NewMain")
public class NewMain extends OpMode {

    // シューター速度 PID 係数 (Const.Shooter.PID と同じ)
    private static final double SHOOTER_KP = 0.00743;
    private static final double SHOOTER_KI = 0.000000000002;
    private static final double SHOOTER_KD = 0.0;

    // 目標速度。DcMotorEx.getVelocity() の単位 (ticks/sec) で扱う。
    private static final double SHOOTER_TARGET = 1000;

    private static final double INTAKE_POWER = 1.0;
    private static final double FEEDER_POWER = 1.0;
    private static final double FEEDER_NUDGE_POWER = 0.3;
    private static final double FEEDER_NUDGE_SECONDS = 0.6;

    // 吐き出し (outtake) — 詰まり解消用に逆回転
    private static final double OUTTAKE_SHOOTER_POWER = -0.2;  // 直接 setPower、PID バイパス
    private static final double OUTTAKE_FEEDER_POWER = -1.0;
    private static final double OUTTAKE_INTAKE_POWER = -1.0;

    // ハードウェア (init() で取得)
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private DcMotor intake;
    private DcMotor feeder;
    private DcMotorEx shooter;

    private ControlSystem shooterPid;

    // Feeder nudge 状態
    private final ElapsedTime feederNudgeTimer = new ElapsedTime();
    private boolean nudgeActive = false;
    private boolean lastB = false;

    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        // ===== ハードウェア取得 =====
        leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        leftBack = hardwareMap.get(DcMotor.class, "leftRear");
        rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        rightBack = hardwareMap.get(DcMotor.class, "rightRear");
        intake = hardwareMap.get(DcMotor.class, "IntakeMotor");
        feeder = hardwareMap.get(DcMotor.class, "FeederMotor");
        shooter = hardwareMap.get(DcMotorEx.class, "ShooterMotor");

        leftFront.setDirection(DcMotorSimple.Direction.REVERSE);
        leftBack.setDirection(DcMotorSimple.Direction.REVERSE);
        rightFront.setDirection(DcMotorSimple.Direction.FORWARD);
        rightBack.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        feeder.setDirection(DcMotorSimple.Direction.REVERSE);
        shooter.setDirection(DcMotorSimple.Direction.FORWARD);

        for (DcMotor m : new DcMotor[]{leftFront, leftBack, rightFront, rightBack,
                intake, feeder, shooter}) {
            m.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        // ===== シューター速度 PID =====
        shooterPid = ControlSystem.builder()
                .velPid(SHOOTER_KP, SHOOTER_KI, SHOOTER_KD)
                .build();
        shooterPid.setGoal(new KineticState(0.0, 0.0));

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        runtime.reset();

        // Shooter PID 起動 + Intake ON
        shooterPid.setGoal(new KineticState(0.0, SHOOTER_TARGET));
        intake.setPower(INTAKE_POWER);
    }

    @Override
    public void loop() {
        // ===== ボタン読み取り =====
        double driveAxial = -gamepad1.left_stick_y;
        double driveLateral = gamepad1.left_stick_x;
        double driveYaw = gamepad1.right_stick_x;
        boolean shoot = gamepad1.a;
        boolean nudgePress = gamepad1.b;
        boolean outtake = gamepad1.y;
        boolean intakeOn = gamepad1.left_bumper;
        boolean intakeOff = gamepad1.right_bumper;

        // ===== ドライブ (POV mecanum) =====
        drive(driveAxial, driveLateral, driveYaw);

        // ===== B nudge エッジ検出 (outtake 中も状態だけ更新しておく) =====
        if (nudgePress && !lastB) {
            feederNudgeTimer.reset();
            nudgeActive = true;
        }
        lastB = nudgePress;
        if (nudgeActive && feederNudgeTimer.seconds() >= FEEDER_NUDGE_SECONDS) {
            nudgeActive = false;
        }

        if (outtake) {
            // ===== 吐き出し: 全部逆回転で上書き、shooter は PID バイパス =====
            intake.setPower(OUTTAKE_INTAKE_POWER);
            feeder.setPower(OUTTAKE_FEEDER_POWER);
            shooter.setPower(OUTTAKE_SHOOTER_POWER);
        } else {
            // ===== Intake (bumper で ON/OFF 切替、setPower は冪等なので edge 検出不要) =====
            if (intakeOn) {
                intake.setPower(INTAKE_POWER);
            } else if (intakeOff) {
                intake.setPower(0.0);
            }

            // ===== Feeder =====
            if (shoot) {
                feeder.setPower(FEEDER_POWER);
            } else if (nudgeActive) {
                feeder.setPower(FEEDER_NUDGE_POWER);
            } else {
                feeder.setPower(0.0);
            }

            // ===== Shooter (PID) =====
            shooter.setPower(shooterPid.calculate(
                    new KineticState(0.0, shooter.getVelocity())));
        }

        // ===== Telemetry (OpMode は loop() 終了時に自動 update) =====
        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Drive LF/RF",
                String.format(Locale.ROOT, "%4.2f, %4.2f",
                        leftFront.getPower(), rightFront.getPower()));
        telemetry.addData("Drive LB/RB",
                String.format(Locale.ROOT, "%4.2f, %4.2f",
                        leftBack.getPower(), rightBack.getPower()));
        telemetry.addData("Shooter (act/target)",
                String.format(Locale.ROOT, "%.0f / %.0f",
                        shooter.getVelocity(), shooterPid.getGoal().getVelocity()));
        telemetry.addData("Feeder", feederStatus(shoot, outtake));
    }

    /**
     * メカナム POV ドライブ。各成分は -1..1 を想定。合計が 1 を超える場合は等倍縮小して
     * モーターパワーが 1 を超えないように正規化する。
     */
    private void drive(double axial, double lateral, double yaw) {
        double lf = axial + lateral + yaw;
        double rf = axial - lateral - yaw;
        double lb = axial - lateral + yaw;
        double rb = axial + lateral - yaw;

        double max = Math.max(
                Math.max(Math.abs(lf), Math.abs(rf)),
                Math.max(Math.abs(lb), Math.abs(rb)));
        if (max > 1.0) {
            lf /= max;
            rf /= max;
            lb /= max;
            rb /= max;
        }
        leftFront.setPower(lf);
        rightFront.setPower(rf);
        leftBack.setPower(lb);
        rightBack.setPower(rb);
    }

    private String feederStatus(boolean shoot, boolean outtake) {
        if (outtake) {
            return "OUTTAKE";
        }
        if (shoot) {
            return "SHOOT";
        }
        if (nudgeActive) {
            return String.format(Locale.ROOT, "NUDGE %.2fs", feederNudgeTimer.seconds());
        }
        return "OFF";
    }
}
