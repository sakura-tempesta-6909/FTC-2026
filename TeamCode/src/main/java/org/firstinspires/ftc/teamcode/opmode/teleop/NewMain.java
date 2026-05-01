package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.bindings.BindingManager;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.ftc.Gamepads;

import java.util.Locale;

/**
 * シンプルな手動操縦 OpMode。
 * <ul>
 *   <li>メカナムドライブ: gamepad1 左スティック (移動) / 右スティック X (旋回)
 *   <li>Shooter: Start 後 PID で目標 RPM 維持
 *   <li>Intake: Start 直後 ON、leftBumper = ON / rightBumper = OFF で切替
 *   <li>Feeder: gamepad1.a ホールドで通常フィード、gamepad1.b で短時間 nudge
 * </ul>
 */
@TeleOp(name = "NewMain")
public class NewMain extends LinearOpMode {

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

    // gamepad1.b のラムダ内で参照/代入するためフィールドで保持する。
    private final ElapsedTime feederNudgeTimer = new ElapsedTime();
    private boolean nudgeActive = false;

    @Override
    public void runOpMode() {
        // ===== ハードウェア取得 =====
        DcMotor leftFront = hardwareMap.get(DcMotor.class, "leftFront");
        DcMotor leftBack = hardwareMap.get(DcMotor.class, "leftRear");
        DcMotor rightFront = hardwareMap.get(DcMotor.class, "rightFront");
        DcMotor rightBack = hardwareMap.get(DcMotor.class, "rightRear");
        DcMotor intake = hardwareMap.get(DcMotor.class, "IntakeMotor");
        DcMotor feeder = hardwareMap.get(DcMotor.class, "FeederMotor");
        DcMotorEx shooter = hardwareMap.get(DcMotorEx.class, "ShooterMotor");

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
        ControlSystem shooterPid = ControlSystem.builder()
                .velPid(SHOOTER_KP, SHOOTER_KI, SHOOTER_KD)
                .build();
        shooterPid.setGoal(new KineticState(0.0, 0.0));

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        // Start 直後: Shooter PID 起動 + Intake ON
        shooterPid.setGoal(new KineticState(0.0, SHOOTER_TARGET));
        intake.setPower(INTAKE_POWER);

        // Intake は bumper で ON/OFF を切替 (NextFTC Bindings)
        Gamepads.gamepad1().leftBumper()
                .whenBecomesTrue(() -> intake.setPower(INTAKE_POWER));
        Gamepads.gamepad1().rightBumper()
                .whenBecomesTrue(() -> intake.setPower(0.0));

        // gamepad1.b の立ち上がりで feeder を短時間 nudge
        Gamepads.gamepad1().b().whenBecomesTrue(() -> {
            feederNudgeTimer.reset();
            nudgeActive = true;
        });

        ElapsedTime runtime = new ElapsedTime();
        while (opModeIsActive()) {
            BindingManager.update();

            // ===== ドライブ (POV mecanum) =====
            double axial = -gamepad1.left_stick_y;
            double lateral = gamepad1.left_stick_x;
            double yaw = gamepad1.right_stick_x;

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

            // ===== Feeder =====
            if (nudgeActive && feederNudgeTimer.seconds() >= FEEDER_NUDGE_SECONDS) {
                nudgeActive = false;
            }
            boolean feedHold = gamepad1.a;
            if (nudgeActive) {
                feeder.setPower(FEEDER_NUDGE_POWER);
            } else if (feedHold) {
                feeder.setPower(FEEDER_POWER);
            } else {
                feeder.setPower(0.0);
            }

            // ===== Shooter (PID) =====
            shooter.setPower(shooterPid.calculate(
                    new KineticState(0.0, shooter.getVelocity())));

            // ===== Telemetry =====
            telemetry.addData("Run Time", runtime.toString());
            telemetry.addData("Drive LF/RF",
                    String.format(Locale.ROOT, "%4.2f, %4.2f", lf, rf));
            telemetry.addData("Drive LB/RB",
                    String.format(Locale.ROOT, "%4.2f, %4.2f", lb, rb));
            telemetry.addData("Shooter (act/target)",
                    String.format(Locale.ROOT, "%.0f / %.0f",
                            shooter.getVelocity(), shooterPid.getGoal().getVelocity()));
            telemetry.addData("Feeder", feederStatus(feedHold));
            telemetry.update();
        }
    }

    private String feederStatus(boolean feedHold) {
        if (feedHold) {
            return "HOLD";
        }
        if (nudgeActive) {
            return String.format(Locale.ROOT, "NUDGE %.2fs", feederNudgeTimer.seconds());
        }
        return "OFF";
    }
}
