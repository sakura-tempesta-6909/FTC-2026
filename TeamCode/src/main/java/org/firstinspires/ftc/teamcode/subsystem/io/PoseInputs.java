package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * Pedro follower / Limelight のスカラー状態。
 * <p>
 * 位置情報 (x, y, heading) 自体は {@code KoalaLog.logPose2d()} で struct:Pose2d として
 * 別途書き出す (AdvantageScope で Drive Base モデルに流せるように)。
 * このクラスには Pose として構造化しない補助値だけ入れる。
 */
@AutoLog
public class PoseInputs {
    // --- Limelight 検出有効性 ---
    public boolean limelightValid;

    // --- 速度 (cm/s 相当、Pedro の内部単位) ---
    public double velocityMag;

    // --- Pedro follower / path 追従状況 ---
    public boolean pathFollowing;
    public double pathTValue;   // 現在パス内の進行度 0..1
    public double pathNumber;   // PathChain 内のサブパス番号
}
