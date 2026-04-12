package org.firstinspires.ftc.teamcode.subsystem;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.ActiveOpMode;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.lib.Motif;
import org.firstinspires.ftc.teamcode.lib.TagDetectionHistory;
import org.firstinspires.ftc.teamcode.lib.TagDetectionRecord;

import java.util.List;

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

    /** AprilTag 検出履歴。MOTIF 多数決判定と練習後分析に使用。 */
    private final TagDetectionHistory detectionHistory = new TagDetectionHistory();

    /** OpMode 開始からの経過時間。検出記録のタイムスタンプに使用。 */
    private final ElapsedTime timer = new ElapsedTime();

    /** 最新フレームのターゲット水平角度 (度)。タグ未検出なら 0。 */
    private double tx;

    /** 最新フレームのターゲット垂直角度 (度)。タグ未検出なら 0。 */
    private double ty;

    /** 最新フレームのレイテンシ (ms)。 */
    private double latencyMs;

    private LimelightSubsystem() {}

    @Override
    public void initialize() {
        limelight = ActiveOpMode.hardwareMap().get(Limelight3A.class, Const.Limelight.DEVICE_NAME);
        limelight.pipelineSwitch(Const.Limelight.PIPELINE);
        limelight.start();
        distance = 0;
        limelightPose = null;
        tx = 0;
        ty = 0;
        latencyMs = 0;
        detectionHistory.clear();
        timer.reset();
    }

    @Override
    public void periodic() {
        // Pedro の heading (radians) を degrees に変換して Limelight に渡す
        Pose robotPose = PedroComponent.follower().getPose();
        // Pedro heading → Limelight heading: +90° (Pedro は 0°=右、Limelight/FTC は 0°=奥)
        double headingDeg = Math.toDegrees(robotPose.getHeading()) + 90;
        limelight.updateRobotOrientation(headingDeg);

        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            limelightPose = computeLimelightPose(result);
            distance = computeDistanceToGoal(result);
            tx = result.getTx();
            ty = result.getTy();
            latencyMs = result.getCaptureLatency() + result.getTargetingLatency();

            // 個別タグの検出履歴を記録
            recordFiducials(result, robotPose);
        } else {
            distance = 0;
            limelightPose = null;
            tx = 0;
            ty = 0;
        }

        // テレメトリ (update() は Main.onUpdate で一括送信)
        var telemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        // --- MOTIF ---
        Motif voted = detectionHistory.getVotedMotif();
        if (voted != null) {
            telemetry.addData("[MOTIF] 判定",
                    String.format("%s  確度:%.0f%%",
                            voted.getMotifName(),
                            detectionHistory.getConfidence() * 100));
            telemetry.addData("[MOTIF] パターン", voted.getPatternString());
        } else {
            int count = detectionHistory.getObeliskDetectionCount();
            telemetry.addData("[MOTIF] 判定",
                    count == 0 ? "未検出" : String.format("検出中... (%d回)", count));
        }

        // --- 照準 ---
        boolean tagVisible = (result != null && result.isValid());
        telemetry.addData("[照準] 状態", tagVisible ? "検出中" : "---");
        telemetry.addData("[照準] 水平(tx)", String.format("%.1f°", tx));
        telemetry.addData("[照準] 垂直(ty)", String.format("%.1f°", ty));
        telemetry.addData("[照準] 距離", String.format("%.0fcm", distance));
        telemetry.addData("[照準] 遅延", String.format("%.0fms", latencyMs));

        // --- 位置 (常に表示) ---
        telemetry.addData("[位置] Odo",
                String.format("(%.1f, %.1f) h:%.1f°",
                        robotPose.getX(), robotPose.getY(),
                        Math.toDegrees(robotPose.getHeading())));
        if (tagVisible && limelightPose != null) {
            // 検出タグ ID
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
            if (fiducials != null && !fiducials.isEmpty()) {
                StringBuilder ids = new StringBuilder();
                for (LLResultTypes.FiducialResult fr : fiducials) {
                    if (ids.length() > 0) ids.append(", ");
                    ids.append(fr.getFiducialId());
                }
                telemetry.addData("[位置] タグ", ids.toString());
            }
            telemetry.addData("[位置] LL",
                    String.format("(%.1f, %.1f) h:%.1f°",
                            limelightPose.getX(), limelightPose.getY(),
                            Math.toDegrees(limelightPose.getHeading())));
            double dx = limelightPose.getX() - robotPose.getX();
            double dy = limelightPose.getY() - robotPose.getY();
            telemetry.addData("[位置] 差分",
                    String.format("%.1fin (dx:%.1f dy:%.1f)", Math.sqrt(dx * dx + dy * dy), dx, dy));
        } else {
            telemetry.addData("[位置] LL", "---");
        }
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

    /** AprilTag 検出履歴。 */
    public TagDetectionHistory getDetectionHistory() {
        return detectionHistory;
    }

    /** 多数決で判定された MOTIF。未確定なら null。 */
    public Motif getVotedMotif() {
        return detectionHistory.getVotedMotif();
    }

    /** ターゲットへの水平角度 (度)。 */
    public double getTx() { return tx; }

    /** ターゲットへの垂直角度 (度)。 */
    public double getTy() { return ty; }

    // --- 計算ヘルパ ---

    // GOAL タグの FTC 座標 (メートル) — AprilTagGameDatabase より
    private static final double BLUE_GOAL_X = -58.3727 / Const.Limelight.CoordinateConversion.METERS_TO_INCHES;
    private static final double BLUE_GOAL_Y = -55.6425 / Const.Limelight.CoordinateConversion.METERS_TO_INCHES;
    private static final double RED_GOAL_X = -58.3727 / Const.Limelight.CoordinateConversion.METERS_TO_INCHES;
    private static final double RED_GOAL_Y = 55.6425 / Const.Limelight.CoordinateConversion.METERS_TO_INCHES;

    /**
     * botpose_MT2 と GOAL タグの既知位置から地表面距離 (cm) を計算する。
     * 検出タグが GOAL タグ (20/24) ならそのタグへの距離、
     * それ以外なら最も近い GOAL への距離を返す。
     */
    private static double computeDistanceToGoal(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();
        if (botpose == null) return 0;

        double robotX = botpose.getPosition().x;
        double robotY = botpose.getPosition().y;

        // 検出タグ ID に基づいて対象 GOAL を選択
        double goalX, goalY;
        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        int detectedGoalId = findGoalTagId(fiducials);
        if (detectedGoalId == 20) {
            goalX = BLUE_GOAL_X;
            goalY = BLUE_GOAL_Y;
        } else if (detectedGoalId == 24) {
            goalX = RED_GOAL_X;
            goalY = RED_GOAL_Y;
        } else {
            // GOAL タグ未検出 → 最も近い GOAL を使用
            double distBlue = Math.hypot(robotX - BLUE_GOAL_X, robotY - BLUE_GOAL_Y);
            double distRed = Math.hypot(robotX - RED_GOAL_X, robotY - RED_GOAL_Y);
            if (distBlue < distRed) {
                goalX = BLUE_GOAL_X;
                goalY = BLUE_GOAL_Y;
            } else {
                goalX = RED_GOAL_X;
                goalY = RED_GOAL_Y;
            }
        }

        return Math.hypot(robotX - goalX, robotY - goalY) * 100; // メートル → cm
    }

    /** Fiducial 結果から GOAL タグ ID (20 or 24) を探す。見つからなければ -1。 */
    private static int findGoalTagId(List<LLResultTypes.FiducialResult> fiducials) {
        if (fiducials == null) return -1;
        for (LLResultTypes.FiducialResult fr : fiducials) {
            int id = fr.getFiducialId();
            if (id == 20 || id == 24) return id;
        }
        return -1;
    }

    /**
     * Limelight botpose_MT2 を Pedro 座標系に変換する。
     * <p>
     * DECODE の Limelight/FTC 座標系と Pedro 座標系の対応:
     * <ul>
     *   <li>Pedro X (Blue→Red) = LL Y (メートル→インチ) + 72</li>
     *   <li>Pedro Y (観客→ゴール) = -LL X (メートル→インチ) + 72</li>
     *   <li>Pedro Heading = LL Yaw - 90° (FTC 0°=奥 → Pedro 0°=右)</li>
     * </ul>
     */
    private static Pose computeLimelightPose(LLResult result) {
        Pose3D botpose = result.getBotpose_MT2();
        if (botpose == null) return null;
        double llXInches = botpose.getPosition().x * Const.Limelight.CoordinateConversion.METERS_TO_INCHES;
        double llYInches = botpose.getPosition().y * Const.Limelight.CoordinateConversion.METERS_TO_INCHES;
        // FTC heading (0°=奥) → Pedro heading (0°=右): -90°
        double heading = Math.toRadians(botpose.getOrientation().getYaw() - 90);
        // 軸スワップ + オフセット
        double pedroX = llYInches + 72;
        double pedroY = -llXInches + 72;
        return new Pose(pedroX, pedroY, heading);
    }

    /** LLResult から個別 Fiducial (AprilTag) を取り出して検出履歴に記録する。 */
    private void recordFiducials(LLResult result, Pose robotPose) {
        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        if (fiducials == null) return;
        double elapsed = timer.seconds();
        for (LLResultTypes.FiducialResult fr : fiducials) {
            detectionHistory.record(new TagDetectionRecord(
                    elapsed,
                    fr.getFiducialId(),
                    fr.getTargetXDegrees(),
                    fr.getTargetYDegrees(),
                    fr.getTargetArea(),
                    robotPose.getX(),
                    robotPose.getY(),
                    robotPose.getHeading()
            ));
        }
    }
}
