package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import java.util.Locale;

import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

/**
 * シンプルな手動操縦 OpMode。役割分担: Driver1 = 足回り + Shoot + Outtake、Driver2 = Nudge + Shooter speed.
 * <h3>Driver 1 (gamepad1)</h3>
 * <ul>
 *   <li>Field-oriented メカナムドライブ: 左スティック (移動) / 右スティック X (旋回)、IMU で field 方向に補正
 *   <li>start: 現在の向きを 0° に再定義 (gyro reset)
 *   <li>a (ホールド): feeder 全力で発射 (Shoot)
 *   <li>y (ホールド): feeder/intake 逆回転 (Outtake、shooter は通常回転維持)
 *   <li>leftBumper / rightBumper: intake ON / OFF
 * </ul>
 * <h3>Driver 2 (gamepad2)</h3>
 * <ul>
 *   <li>b: feeder を短時間ゆっくり回す (Nudge、装填調整)
 *   <li>a / x / y: shooter speed を LOW / MID / HIGH に直接設定
 * </ul>
 */
@TeleOp(name = "RobotOriented_Main")
public class Robotoriented_Main extends OpMode {

    // シューター速度 PID 係数 (Const.Shooter.PID と同じ)
    private static final double SHOOTER_KP = 0.00743;
    private static final double SHOOTER_KI = 0.000000000002;
    private static final double SHOOTER_KD = 0.0;

    // シューター目標速度の 3 段階 (LOW/MID/HIGH)。単位は ticks/sec。
    private static final double[] SHOOTER_SPEEDS = {800, 1000, 1200};
    private static final String[] SHOOTER_SPEED_NAMES = {"LOW", "MID", "HIGH"};
    private static final int DEFAULT_SHOOTER_SPEED_INDEX = 1;  // MID

    private static final double INTAKE_POWER = 1.0;
    private static final double FEEDER_POWER = 1.0;
    private static final double FEEDER_NUDGE_POWER = 0.3;
    private static final double FEEDER_NUDGE_SECONDS = 0.6;

    // Outtake (詰まり解消の逆回転)。shooter は通常 PID のまま。
    private static final double OUTTAKE_FEEDER_POWER = -1.0;
    private static final double OUTTAKE_INTAKE_POWER = -1.0;

    // IMU マウント方向 (Const.Imu と同じ)
    private static final RevHubOrientationOnRobot.LogoFacingDirection IMU_LOGO_DIRECTION =
            RevHubOrientationOnRobot.LogoFacingDirection.LEFT;
    private static final RevHubOrientationOnRobot.UsbFacingDirection IMU_USB_DIRECTION =
            RevHubOrientationOnRobot.UsbFacingDirection.UP;

    // ハードウェア (init() で取得)
    private DcMotor leftFront;
    private DcMotor leftBack;
    private DcMotor rightFront;
    private DcMotor rightBack;
    private DcMotor intake;
    private DcMotor feeder;
    private DcMotorEx shooter;
    private IMU imu;

    private ControlSystem shooterPid;

    // Feeder nudge 状態
    private final ElapsedTime feederNudgeTimer = new ElapsedTime();
    private boolean nudgeActive = false;
    private boolean lastNudge = false;

    // Shooter speed 切替 (dpad で直接選択するので edge 検出不要)
    private int shooterSpeedIndex = DEFAULT_SHOOTER_SPEED_INDEX;

    // Intake の意図状態 (outtake 中は上書きされるが、release 後にここの値へ戻る)
    private boolean intakeRunning = true;

    // IMU yaw の offset。imu.resetYaw() が動作不安定なので自前で 0° 基準を持つ。
    private double headingOffset = 0.0;

    private final ElapsedTime runtime = new ElapsedTime();

    @Override
    public void init() {
        // ----- ハードウェア取得 -----
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

        // ----- IMU -----
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(
                new RevHubOrientationOnRobot(IMU_LOGO_DIRECTION, IMU_USB_DIRECTION)));

        // ----- シューター速度 PID -----
        shooterPid = ControlSystem.builder()
                .velPid(SHOOTER_KP, SHOOTER_KI, SHOOTER_KD)
                .build();
        shooterPid.setGoal(new KineticState(0.0, 0.0));

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void start() {
        // Shooter PID は loop() で毎回 setGoal するのでここでは触らない。
        // gyro: マッチ開始時の向きを 0° に記録 (offset 自前管理)
        runtime.reset();
        headingOffset = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        intake.setPower(INTAKE_POWER);
    }

    @Override
    public void loop() {
        // ----- 入力読み取り -----
        // Driver 1: 足回り、Shoot、Outtake、Intake、Gyro reset
        double driveAxial = -gamepad1.left_stick_y;
        double driveLateral = gamepad1.left_stick_x;
        double driveYaw = gamepad1.right_stick_x;
        boolean shoot = gamepad1.a;
        boolean outtake = gamepad1.y;
        boolean intakeOn = gamepad1.left_bumper;
        boolean intakeOff = gamepad1.right_bumper;
        boolean resetGyro = gamepad1.start;

        // Driver 2: Nudge、Shooter speed (face button で直接選択)
        boolean nudgePress = gamepad2.b;
        boolean speedLow = gamepad2.a;  // ×  (下)
        boolean speedMid = gamepad2.x;  // □  (左)
        boolean speedHigh = gamepad2.y;  // △  (上)

        // ----- Gyro リセット (押している間ずっと再ゼロ化、離したタイミングの向きが 0° に固定) -----
        if (resetGyro) {
            headingOffset = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);
        }

        // ----- Shooter speed 直接選択 -----
        // 押されたボタンに対応する index に固定。何も押されてなければ前回値を維持。
        if (speedLow) shooterSpeedIndex = 0;
        if (speedMid) shooterSpeedIndex = 1;
        if (speedHigh) shooterSpeedIndex = 2;
        shooterPid.setGoal(new KineticState(0.0, SHOOTER_SPEEDS[shooterSpeedIndex]));

        // ----- ドライブ -----
        // 切替: 下のどちらか 1 行だけ有効にする。
        double heading = AngleUnit.RADIANS.normalize(
                imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS) - headingOffset);
        //driveFieldOriented(driveAxial, driveLateral, driveYaw, heading);
        drive(driveAxial, driveLateral, driveYaw);  // ← Robot-Centric に切替えるならこちら

        // ----- Nudge エッジ検出 (outtake 中も状態だけ更新) -----
        if (nudgePress && !lastNudge) {
            feederNudgeTimer.reset();
            nudgeActive = true;
        }
        lastNudge = nudgePress;
        if (nudgeActive && feederNudgeTimer.seconds() >= FEEDER_NUDGE_SECONDS) {
            nudgeActive = false;
        }

        // ----- Intake -----
        // bumper で意図状態を更新。outtake 中は逆回転で一時上書き
        if (intakeOn) intakeRunning = true;
        if (intakeOff) intakeRunning = false;
        double normalIntakePower = intakeRunning ? INTAKE_POWER : 0.0;
        intake.setPower(outtake ? OUTTAKE_INTAKE_POWER : normalIntakePower);

        // ----- Feeder -----
        // shoot / nudge で通常パワーを決め、outtake 中は逆回転で一時上書き
        double normalFeederPower;
        if (shoot) normalFeederPower = FEEDER_POWER;
        else if (nudgeActive) normalFeederPower = FEEDER_NUDGE_POWER;
        else normalFeederPower = 0.0;
        feeder.setPower(outtake ? OUTTAKE_FEEDER_POWER : normalFeederPower);

        // ----- Shooter (PID、outtake 中も通常稼働) -----
        shooter.setPower(shooterPid.calculate(
                new KineticState(0.0, shooter.getVelocity())));

        // ----- Telemetry -----
        // OpMode は loop() 終了時に自動 update
        // 最上段に Shooter speed バナー (driver から見やすく)
        telemetry.addLine("================================");
        telemetry.addLine(String.format(Locale.ROOT, "  >>>  SHOOTER: %s  (%.0f)  <<<",
                SHOOTER_SPEED_NAMES[shooterSpeedIndex], SHOOTER_SPEEDS[shooterSpeedIndex]));
        telemetry.addLine("================================");

        telemetry.addData("Run Time", runtime.toString());
        telemetry.addData("Heading",
                String.format(Locale.ROOT, "%.1f°", Math.toDegrees(heading)));
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
     * ロボット座標系メカナムドライブ (Robot-Centric)。
     * (axial, lateral, yaw) はロボットフレームの指令そのまま。
     * 合計が 1 を超える場合はホイールパワーを等倍縮小して飽和を防ぐ。
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

    /**
     * Field-Oriented メカナムドライブ。
     * <p>
     * (fieldAxial, fieldLateral) を heading で回転してロボット座標に変換し {@link #drive} に渡す。
     * heading は IMU yaw (rad、CCW 正)。
     */
    private void driveFieldOriented(double fieldAxial, double fieldLateral, double yaw, double heading) {
        double cosH = Math.cos(heading);
        double sinH = Math.sin(heading);
        double axial = fieldAxial * cosH - fieldLateral * sinH;
        double lateral = fieldAxial * sinH + fieldLateral * cosH;
        drive(axial, lateral, yaw);
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
