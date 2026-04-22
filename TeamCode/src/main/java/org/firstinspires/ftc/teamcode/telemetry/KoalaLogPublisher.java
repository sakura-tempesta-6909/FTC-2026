package org.firstinspires.ftc.teamcode.telemetry;

import Ori.Coval.Logging.AutoLogManager;
import Ori.Coval.Logging.Logger.KoalaLog;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.paths.PathPoint;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.lib.FieldCoordinates;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.io.*;

/**
 * KoalaLog への出力を担当する Publisher。
 * <p>
 * 各 Subsystem が自身の {@code *InputsAutoLogged} を持つ設計だが、
 * Phase 1 段階では Subsystem 側を触らず、HardwareMap 経由で直接モーター値を読み出して
 * Inputs を埋める簡易実装にしてある。BulkRead が有効なら追加の CAN 通信は発生しない。
 * <p>
 * 責務:
 * <ol>
 *   <li>各 {@code *InputsAutoLogged} をモーター状態で埋める</li>
 *   <li>{@link AutoLogManager#periodic()} を呼び、全登録済み {@code Logged} を一括 wpilog 出力</li>
 * </ol>
 */
public class KoalaLogPublisher {
    // === Inputs (コンストラクタで AutoLogManager.register されるので単純生成で OK) ===
    public static final SystemInputsAutoLogged systemInputs = new SystemInputsAutoLogged();
    public static final ShooterInputsAutoLogged shooterInputs = new ShooterInputsAutoLogged();
    public static final FeederInputsAutoLogged feederInputs = new FeederInputsAutoLogged();
    public static final IntakeInputsAutoLogged intakeInputs = new IntakeInputsAutoLogged();
    public static final DriveInputsAutoLogged driveInputs = new DriveInputsAutoLogged();
    public static final PoseInputsAutoLogged poseInputs = new PoseInputsAutoLogged();

    // === モーター参照 (OpMode セッションごとに init で再取得) ===
    private static HardwareMap cachedHw;
    private static DcMotorEx shooterMotor, feederMotor, intakeMotor;
    private static DcMotorEx leftFront, rightFront, leftRear, rightRear;

    private KoalaLogPublisher() {
    }

    /**
     * モーター参照を HardwareMap から解決してキャッシュする。
     * FTCBaseOpMode.onInit() で KoalaLog.setup() の直後に呼ばれる想定。
     */
    public static void init(HardwareMap hw) {
        cachedHw = hw;
        shooterMotor = hw.get(DcMotorEx.class, Const.Shooter.Motor.NAME);
        feederMotor = hw.get(DcMotorEx.class, Const.Feeder.Motor.NAME);
        intakeMotor = hw.get(DcMotorEx.class, Const.Intake.Motor.NAME);
        leftFront = hw.get(DcMotorEx.class, Const.Drive.Motor.LEFT_FRONT);
        rightFront = hw.get(DcMotorEx.class, Const.Drive.Motor.RIGHT_FRONT);
        leftRear = hw.get(DcMotorEx.class, Const.Drive.Motor.LEFT_REAR);
        rightRear = hw.get(DcMotorEx.class, Const.Drive.Motor.RIGHT_REAR);
    }

    public static void update(HardwareMap hw, double loopSeconds) {
        if (!TelemetryConfig.ENABLE_KOALA_LOG) return;

        // OpMode 切替時の保険 (init が呼ばれていない場合も自動リカバリ)
        if (hw != cachedHw) init(hw);

        // --- System ---
        double batteryV = hw.voltageSensor.iterator().next().getVoltage();
        systemInputs.batteryVoltage = batteryV;
        systemInputs.loopMs = loopSeconds * 1000.0;
        systemInputs.runningCmds = String.join("|", CommandManager.INSTANCE.snapshot());

        // --- Shooter ---
        shooterInputs.power = shooterMotor.getPower();
        shooterInputs.voltageV = batteryV * shooterInputs.power;
        shooterInputs.currentA = shooterMotor.getCurrent(CurrentUnit.AMPS);
        shooterInputs.velocity = shooterMotor.getVelocity();

        // --- Feeder ---
        feederInputs.power = feederMotor.getPower();
        feederInputs.voltageV = batteryV * feederInputs.power;
        feederInputs.currentA = feederMotor.getCurrent(CurrentUnit.AMPS);

        // --- Intake ---
        intakeInputs.power = intakeMotor.getPower();
        intakeInputs.voltageV = batteryV * intakeInputs.power;
        intakeInputs.currentA = intakeMotor.getCurrent(CurrentUnit.AMPS);

        // --- Drive (4 輪) ---
        driveInputs.leftFrontPower = leftFront.getPower();
        driveInputs.rightFrontPower = rightFront.getPower();
        driveInputs.leftRearPower = leftRear.getPower();
        driveInputs.rightRearPower = rightRear.getPower();

        driveInputs.leftFrontVoltageV = batteryV * driveInputs.leftFrontPower;
        driveInputs.rightFrontVoltageV = batteryV * driveInputs.rightFrontPower;
        driveInputs.leftRearVoltageV = batteryV * driveInputs.leftRearPower;
        driveInputs.rightRearVoltageV = batteryV * driveInputs.rightRearPower;

        driveInputs.leftFrontCurrentA = leftFront.getCurrent(CurrentUnit.AMPS);
        driveInputs.rightFrontCurrentA = rightFront.getCurrent(CurrentUnit.AMPS);
        driveInputs.leftRearCurrentA = leftRear.getCurrent(CurrentUnit.AMPS);
        driveInputs.rightRearCurrentA = rightRear.getCurrent(CurrentUnit.AMPS);

        driveInputs.leftFrontVelocity = leftFront.getVelocity();
        driveInputs.rightFrontVelocity = rightFront.getVelocity();
        driveInputs.leftRearVelocity = leftRear.getVelocity();
        driveInputs.rightRearVelocity = rightRear.getVelocity();

        // --- Pose / Path (Pedro + Limelight) ---
        updatePose();

        // 全 @AutoLog 登録済みクラスの toLog() を呼ぶ → wpilog に書き込む
        AutoLogManager.periodic();
    }

    private static void updatePose() {
        Follower follower = PedroComponent.follower();
        if (follower != null) {
            Pose odo = follower.getPose();
            if (odo != null) {
                publishPose("Pose/Odo", odo);
            }
            Vector vel = follower.getVelocity();
            if (vel != null) {
                poseInputs.velocityMag = FieldCoordinates.pedroLenToM(vel.getMagnitude());
            }
            poseInputs.pathFollowing = follower.isBusy();
            if (follower.isBusy()) {
                poseInputs.pathTValue = follower.getCurrentTValue();
                poseInputs.pathNumber = follower.getCurrentPathNumber();
                PathPoint closest = follower.getClosestPose();
                if (closest != null && closest.getPose() != null) {
                    publishPose("Pose/PathClosest", closest.getPose());
                }
            }
        }

        Pose llPose = LimelightSubsystem.INSTANCE.getLimelightPose();
        poseInputs.limelightValid = llPose != null;
        if (llPose != null) {
            publishPose("Pose/Limelight", llPose);
        }
    }

    /**
     * Pose を AdvantageScope の 2D/3D Field Drive Base モデルで読める形式で publish する。
     * <p>wpilog を AdvantageScope に読み込ませる前提で、struct:Pose2d 形式で記録する。
     * Drive Base の Source に {@code name} を直接指定するだけで認識される。
     */
    private static void publishPose(String name, Pose pedroPose) {
        double x = FieldCoordinates.pedroToFieldX(pedroPose.getX(), pedroPose.getY());
        double y = FieldCoordinates.pedroToFieldY(pedroPose.getX(), pedroPose.getY());
        double heading = FieldCoordinates.pedroToFieldHeading(pedroPose.getHeading());
        KoalaLog.logPose2d(name, x, y, heading, false);
    }
}
