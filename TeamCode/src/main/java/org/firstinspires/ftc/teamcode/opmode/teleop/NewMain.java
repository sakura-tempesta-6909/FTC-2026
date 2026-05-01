/*
Copyright 2026 FIRST Tech Challenge Team 25787

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode.opmode.teleop;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;

/**
 * This file contains a minimal example of a Linear "OpMode". An OpMode is a 'program' that runs
 * in either the autonomous or the TeleOp period of an FTC match. The names of OpModes appear on
 * the menu of the FTC Driver Station. When an selection is made from the menu, the corresponding
 * OpMode class is instantiated on the Robot Controller and executed.
 *
 * Remove the @Disabled annotation on the next line or two (if present) to add this OpMode to the
 * Driver Station OpMode list, or add a @Disabled annotation to prevent this OpMode from being
 * added to the Driver Station.
 */
@TeleOp

public class NewMain extends LinearOpMode {
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor leftBackDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor rightBackDrive = null;
    private DcMotor intake = null;
    private DcMotor blender = null;
    private DcMotorEx shooter = null;

    // シューター速度 PID 係数 (Const.Shooter.PID と同じ)
    private static final double SHOOTER_KP = 0.00743;
    private static final double SHOOTER_KI = 0.000000000002;
    private static final double SHOOTER_KD = 0.0;

    // 目標速度。getVelocity() の単位 (ticks/sec) と揃える前提。
    private static final double SHOOTER_TARGET = 1280;
    private static final double SHOOTER_STOP = 0.0;

    // Intake / Feeder 出力
    private static final double INTAKE_POWER = 1.0;
    private static final double FEEDER_POWER = 0.7;

    // パルス feed の長さ (秒)
    private static final double FEEDER_PULSE_SECONDS = 1.0;

    private final ElapsedTime feederPulseTimer = new ElapsedTime();
    private boolean pulseActive = false;
    private boolean lastPulseButton = false;


    @Override
    public void runOpMode() {

        leftFrontDrive  = hardwareMap.get(DcMotor.class, "leftFront");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "leftRear");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront");
        rightBackDrive = hardwareMap.get(DcMotor.class, "rightRear");
        intake  = hardwareMap.get(DcMotor.class, "IntakeMotor");
        blender = hardwareMap.get(DcMotor.class, "FeederMotor");
        shooter = hardwareMap.get(DcMotorEx.class, "ShooterMotor");

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);
        leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake.setDirection(DcMotor.Direction.FORWARD);
        blender.setDirection(DcMotor.Direction.REVERSE);
        shooter.setDirection(DcMotor.Direction.FORWARD);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        blender.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shooter.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // シューター速度 PID。停止時 (target=0) はパワーを直接 0 にする。
        ControlSystem shooterPid = ControlSystem.builder()
                .velPid(SHOOTER_KP, SHOOTER_KI, SHOOTER_KD)
                .build();
        shooterPid.setGoal(new KineticState(0.0, SHOOTER_STOP));

        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // Start 後: Shooter と Intake は常時回転 (毎ループ呼ぶ必要はない)
        intake.setPower(INTAKE_POWER);
        shooterPid.setGoal(new KineticState(0.0, SHOOTER_TARGET));

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x;
            double yaw     =  gamepad1.right_stick_x;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = axial + lateral + yaw;
            double rightFrontPower = axial - lateral - yaw;
            double leftBackPower   = axial - lateral + yaw;
            double rightBackPower  = axial + lateral - yaw;

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);


            // ===== Feeder 制御 =====
            // gamepad1.a: 押している間だけ回す (ホールド)
            // gamepad1.b: 押した瞬間に 1 秒間だけ回す (パルス)
            boolean currentPulseButton = gamepad1.b;
            if (currentPulseButton && !lastPulseButton) {
                feederPulseTimer.reset();
                pulseActive = true;
            }
            lastPulseButton = currentPulseButton;

            if (pulseActive && feederPulseTimer.seconds() >= FEEDER_PULSE_SECONDS) {
                pulseActive = false;
            }

            boolean feedHold = gamepad1.a;
            if (feedHold || pulseActive) {
                blender.setPower(FEEDER_POWER);
            } else {
                blender.setPower(0.0);
            }

            // シューター速度 PID 計算 → モーターパワー反映。
            // 目標 0 のときはボールが押し出されないようパワーも 0 にする。
            double shooterPower;
            if (shooterPid.getGoal().getVelocity() == 0.0) {
                shooterPower = 0.0;
            } else {
                shooterPower = shooterPid.calculate(
                        new KineticState(0.0, shooter.getVelocity()));
            }
            shooter.setPower(shooterPower);

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Shooter target/actual", "%.0f / %.0f",
                    shooterPid.getGoal().getVelocity(), shooter.getVelocity());
            telemetry.addData("Feeder",
                    feedHold ? "HOLD"
                            : pulseActive
                            ? String.format("PULSE %.2fs", feederPulseTimer.seconds())
                            : "OFF");
            telemetry.addData("Status", "Running");
            telemetry.update();

        }
    }
}
