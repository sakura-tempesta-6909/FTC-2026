package org.firstinspires.ftc.teamcode.lib;

/**
 * 1 フレーム・1 タグ分の AprilTag 検出記録。
 */
public class TagDetectionRecord {

    private final double elapsedSeconds;
    private final int tagId;
    private final double tx;
    private final double ty;
    private final double ta;
    private final double robotX;
    private final double robotY;
    private final double robotHeading;

    public TagDetectionRecord(double elapsedSeconds, int tagId,
                              double tx, double ty, double ta,
                              double robotX, double robotY, double robotHeading) {
        this.elapsedSeconds = elapsedSeconds;
        this.tagId = tagId;
        this.tx = tx;
        this.ty = ty;
        this.ta = ta;
        this.robotX = robotX;
        this.robotY = robotY;
        this.robotHeading = robotHeading;
    }

    /** OpMode 開始からの経過秒数。 */
    public double getElapsedSeconds() { return elapsedSeconds; }

    /** 検出した AprilTag の ID。 */
    public int getTagId() { return tagId; }

    /** タグへの水平角度 (度)。0 = カメラ中央。 */
    public double getTx() { return tx; }

    /** タグへの垂直角度 (度)。 */
    public double getTy() { return ty; }

    /** タグの見かけ面積 (% of image)。距離の指標。 */
    public double getTa() { return ta; }

    /** 検出時のロボット X 座標 (Pedro 座標系)。 */
    public double getRobotX() { return robotX; }

    /** 検出時のロボット Y 座標 (Pedro 座標系)。 */
    public double getRobotY() { return robotY; }

    /** 検出時のロボット Heading (ラジアン)。 */
    public double getRobotHeading() { return robotHeading; }
}
