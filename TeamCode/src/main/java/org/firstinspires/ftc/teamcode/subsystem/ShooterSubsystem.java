package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import dev.nextftc.control.ControlSystem;
import dev.nextftc.control.KineticState;
import dev.nextftc.control.feedback.PIDCoefficients;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.ftc.ActiveOpMode;
import dev.nextftc.hardware.impl.MotorEx;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.config.PIDTuning;

/**
 * フライホイール式シューターを制御するサブシステム。
 * <p>
 * NextControl の {@link ControlSystem} で速度 PID を毎ループ走らせる ({@link #periodic()})
 * ため、ここに限っては「常時動かす制御ループ」を持つことが正しい設計。
 * Command からは {@link #setTargetVelocity(double)} / {@link #setTargetRPM()} /
 * {@link #stop()} を介して目標速度を指示する。
 * <p>
 * 加えて、Limelight 3A による AprilTag 距離計算結果も保持する。
 */
@Configurable
public class ShooterSubsystem implements Subsystem {

    public static final ShooterSubsystem INSTANCE = new ShooterSubsystem();

    public static PIDCoefficients pidCoefficients =
            new PIDCoefficients(PIDTuning.KP, PIDTuning.KI, PIDTuning.KD);

    private MotorEx shooterMotor;
    private ControlSystem controller;
    private Limelight3A limelight;
    private IMU imu;

    /** 直近フレームで計算された AprilTag までの地表面距離 (cm)。タグが見えない場合は 0。 */
    private double distance;

    private ShooterSubsystem() {}

    @Override
    public void initialize() {
        shooterMotor = new MotorEx(Const.Shooter.Motor.NAME);
        shooterMotor.reverse();
        shooterMotor.brakeMode();
        shooterMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        controller = ControlSystem.builder().velPid(pidCoefficients).build();
        controller.setGoal(new KineticState(0.0, Const.Shooter.Velocity.STOP));

        limelight = ActiveOpMode.hardwareMap().get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        limelight.start();

        imu = ActiveOpMode.hardwareMap().get(IMU.class, Const.Imu.NAME);
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(orientation));
    }

    @Override
    public void periodic() {
        // (1) PID で目標速度に追従
        double power = controller.calculate(
                new KineticState(0.0, shooterMotor.getVelocity()));
        shooterMotor.setPower(power);

        // (2) Limelight に IMU yaw を渡してから最新フレームを取得
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());

        LLResult llResult = limelight.getLatestResult();
        double distanceToTag;
        if (llResult != null && llResult.isValid()) {
            distanceToTag = computeDistanceToTag(llResult.getTa());
            distance = computeGroundDistance(distanceToTag);
        } else {
            distanceToTag = 0;
            distance = 0;
        }

        // (3) テレメトリ (update() は Main.onUpdate で一括送信)
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter target", controller.getGoal().getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter current", shooterMotor.getVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Shooter atVelocity", isAtVelocity());
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Distance", distance);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("DistanceToTag", distanceToTag);
    }

    // ==================== 目標速度 API ====================

    /** 任意の目標速度を直接セットする (RPM ではなくコントローラ単位)。 */
    public void setTargetVelocity(double velocity) {
        controller.setGoal(new KineticState(0.0, velocity));
    }

    /** 直近の Limelight 距離から段階的に目標 RPM を決めてセットする。 */
    public void setTargetRPM() {
        setTargetVelocity(Const.Shooter.rpmFromDistance(distance));
    }

    /** 逆回転 (詰まり解除など) 用の目標速度をセットする。 */
    public void setReverseTargetRPM() {
        setTargetVelocity(Const.Shooter.Velocity.REVERSE_TARGET_RPM);
    }

    /** 目標速度を 0 にしてシューターを停止する。 */
    public void stop() {
        setTargetVelocity(Const.Shooter.Velocity.STOP);
    }

    /** 現在速度が目標速度の許容範囲内かどうか。 */
    public boolean isAtVelocity() {
        return Math.abs(shooterMotor.getVelocity() - controller.getGoal().getVelocity())
                <= Const.Shooter.Velocity.TOLERANCE;
    }

    // ==================== 距離計算ヘルパ (純粋関数) ====================

    /**
     * Limelight の Ta (タグ占有面積%) から AprilTag までの斜距離を算出する。
     * 較正定数は {@link Const.Shooter.DistanceCalibration} を参照。
     */
    private static double computeDistanceToTag(double ta) {
        return Const.Shooter.DistanceCalibration.SCALE
                * Math.pow(ta, Const.Shooter.DistanceCalibration.EXPONENT);
    }

    /**
     * 斜距離からカメラ高さ補正をかけた地表面距離を算出する。
     * Pythagorean: distance = sqrt(distanceToTag^2 - HEIGHT_OFFSET^2)
     */
    private static double computeGroundDistance(double distanceToTag) {
        double squared = Math.pow(distanceToTag, 2)
                - Const.Shooter.DistanceCalibration.HEIGHT_OFFSET_SQUARED;
        return squared > 0 ? Math.sqrt(squared) : 0;
    }
}
