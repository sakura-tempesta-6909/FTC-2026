package org.firstinspires.ftc.teamcode.lib;

import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.PoseHistory;

/**
 * Panels Field にロボット・パス・位置履歴を描画するユーティリティ。
 * TeleOp / Auto 共通で使用する。
 */
public final class Drawing {

    /** ロボットの描画半径 (インチ)。 */
    public static final double ROBOT_RADIUS = 9;

    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    // --- スタイル定義 ---
    private static final Style ROBOT_STYLE = new Style("", "#3F51B5", 0.75);
    private static final Style HISTORY_STYLE = new Style("", "#4CAF50", 0.75);
    private static final Style LIMELIGHT_STYLE = new Style("", "#97699d", 0.75);
    private static final Style DEVIATION_LINE_STYLE = new Style("", "#FFEB3B", 0.4);

    private Drawing() {}

    /** Pedro 座標系のオフセットを適用する。OpMode の onInit で 1 回呼ぶ。 */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * Follower の現在位置・パス・履歴を描画する。
     * Limelight 位置は描画しない。
     */
    public static void drawDebug(Follower follower) {
        drawDebug(follower, null);
    }

    /**
     * Follower の現在位置・パス・履歴に加え、Limelight 推定位置も描画する。
     *
     * @param follower       Pedro Follower
     * @param limelightPose  Limelight から算出した位置 (null なら描画しない)
     */
    public static void drawDebug(Follower follower, Pose limelightPose) {
        if (follower.getCurrentPath() != null) {
            drawPath(follower.getCurrentPath(), ROBOT_STYLE);
            Pose closestPoint = follower.getPointFromPath(
                    follower.getCurrentPath().getClosestPointTValue());
            double headingGoal = follower.getCurrentPath().getHeadingGoal(
                    follower.getCurrentPath().getClosestPointTValue());
            drawRobot(new Pose(closestPoint.getX(), closestPoint.getY(), headingGoal),
                    ROBOT_STYLE);
        }

        drawPoseHistory(follower.getPoseHistory(), HISTORY_STYLE);
        drawRobot(follower.getPose(), HISTORY_STYLE);

        // Limelight 推定位置 + オドメトリとのずれを可視化
        if (limelightPose != null && isOnField(limelightPose)) {
            drawRobot(limelightPose, LIMELIGHT_STYLE);

            // オドメトリ位置と Limelight 推定位置を結ぶ線 (ずれが大きいほど線が長い)
            panelsField.setStyle(DEVIATION_LINE_STYLE);
            panelsField.moveCursor(follower.getPose().getX(), follower.getPose().getY());
            panelsField.line(limelightPose.getX(), limelightPose.getY());
        }

        sendPacket();
    }

    /** 指定位置にロボットを描画する。 */
    public static void drawRobot(Pose pose, Style style) {
        if (pose == null
                || Double.isNaN(pose.getX())
                || Double.isNaN(pose.getY())
                || Double.isNaN(pose.getHeading())) {
            return;
        }

        panelsField.setStyle(style);
        panelsField.moveCursor(pose.getX(), pose.getY());
        panelsField.circle(ROBOT_RADIUS);

        Vector v = pose.getHeadingAsUnitVector();
        v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS);
        double x1 = pose.getX() + v.getXComponent() / 2;
        double y1 = pose.getY() + v.getYComponent() / 2;
        double x2 = pose.getX() + v.getXComponent();
        double y2 = pose.getY() + v.getYComponent();

        panelsField.setStyle(style);
        panelsField.moveCursor(x1, y1);
        panelsField.line(x2, y2);
    }

    /** パスを描画する。 */
    public static void drawPath(Path path, Style style) {
        double[][] points = path.getPanelsDrawingPoints();
        for (int i = 0; i < points[0].length; i++) {
            for (int j = 0; j < points.length; j++) {
                if (Double.isNaN(points[j][i])) {
                    points[j][i] = 0;
                }
            }
        }
        panelsField.setStyle(style);
        panelsField.moveCursor(points[0][0], points[0][1]);
        panelsField.line(points[1][0], points[1][1]);
    }

    /** PathChain 内の全パスを描画する。 */
    public static void drawPath(PathChain pathChain, Style style) {
        for (int i = 0; i < pathChain.size(); i++) {
            drawPath(pathChain.getPath(i), style);
        }
    }

    /** 位置履歴を線で描画する。 */
    public static void drawPoseHistory(PoseHistory poseTracker, Style style) {
        panelsField.setStyle(style);
        int size = poseTracker.getXPositionsArray().length;
        for (int i = 0; i < size - 1; i++) {
            panelsField.moveCursor(
                    poseTracker.getXPositionsArray()[i],
                    poseTracker.getYPositionsArray()[i]);
            panelsField.line(
                    poseTracker.getXPositionsArray()[i + 1],
                    poseTracker.getYPositionsArray()[i + 1]);
        }
    }

    /** 位置がフィールド内 (0〜144 インチ) に収まっているか判定する。 */
    private static boolean isOnField(Pose pose) {
        double margin = 10; // フィールド外でも少しの余裕を持たせる
        return pose.getX() >= -margin && pose.getX() <= 144 + margin
                && pose.getY() >= -margin && pose.getY() <= 144 + margin;
    }

    /** 描画パケットを Panels に送信する。 */
    public static void sendPacket() {
        panelsField.update();
    }
}
