package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.ActiveOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * Limelight 3A による AprilTag 検出・距離算出・位置推定を行うサブシステム。
 * <p>
 * IMU は独自に初期化せず、{@link PedroComponent} の Follower heading を使う。
 * Pedro の heading は既にオドメトリ + IMU で融合済みなので二重初期化を避けられる。
 * <p>
 * {@link #periodic()} で毎ループ Limelight を読み、距離と Pedro 座標系の推定位置を更新する。
 */
@Configurable
public class LimelightSubsystem implements Subsystem {

    public static final LimelightSubsystem INSTANCE = new LimelightSubsystem();

    private Limelight3A limelight;

    /** AprilTag までの地表面距離 (cm)。タグ未検出なら 0。 */
    private double distance;

    /** Limelight botpose を Pedro 座標系に変換した推定位置。タグ未検出なら null。 */
    private Pose limelightPose;

    private LimelightSubsystem() {}

    @Override
    public void initialize() {
        limelight = ActiveOpMode.hardwareMap().get(Limelight3A.class, Const.Limelight.DEVICE_NAME);
        limelight.pipelineSwitch(Const.Limelight.PIPELINE);
        limelight.start();
        distance = 0;
        limelightPose = null;
    }

    @Override
    public void periodic() {
        // Pedro の heading (radians) を degrees に変換して Limelight に渡す
        double headingDeg = Math.toDegrees(PedroComponent.follower().getPose().getHeading());
        limelight.updateRobotOrientation(headingDeg);

        LLResult result = limelight.getLatestResult();
        double distanceToTag;
        if (result != null && result.isValid()) {
            distanceToTag = computeDistanceToTag(result.getTa());
            distance = computeGroundDistance(distanceToTag);
            limelightPose = computeLimelightPose(result);
        } else {
            distanceToTag = 0;
            distance = 0;
            limelightPose = null;
        }

        // テレメトリ (update() は Main.onUpdate で一括送信)
        PanelsTelemetry.INSTANCE.getTelemetry().addData("Distance", distance);
        PanelsTelemetry.INSTANCE.getTelemetry().addData("DistanceToTag", distanceToTag);
    }

    // --- 公開 API ---

    /** AprilTag までの地表面距離 (cm)。タグ未検出なら 0。 */
    public double getDistance() {
        return distance;
    }

    /** Limelight 推定位置 (Pedro 座標系)。タグ未検出なら null。 */
    public Pose getLimelightPose() {
        return limelightPose;
    }

    // --- 計算ヘルパ ---

    private static double computeDistanceToTag(double ta) {
        return Const.Limelight.DistanceCalibration.SCALE
                * Math.pow(ta, Const.Limelight.DistanceCalibration.EXPONENT);
    }

    private static double computeGroundDistance(double distanceToTag) {
        double squared = Math.pow(distanceToTag, 2)
                - Const.Limelight.DistanceCalibration.HEIGHT_OFFSET_SQUARED;
        return squared > 0 ? Math.sqrt(squared) : 0;
    }

    private static Pose computeLimelightPose(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();
        if (botpose == null) return null;
        double x = botpose.getPosition().x * Const.Limelight.CoordinateConversion.METERS_TO_INCHES
                + Const.Limelight.CoordinateConversion.FIELD_OFFSET_INCHES;
        double y = botpose.getPosition().y * Const.Limelight.CoordinateConversion.METERS_TO_INCHES
                + Const.Limelight.CoordinateConversion.FIELD_OFFSET_INCHES;
        double heading = Math.toRadians(botpose.getOrientation().getYaw());
        return new Pose(x, y, heading);
    }
}
