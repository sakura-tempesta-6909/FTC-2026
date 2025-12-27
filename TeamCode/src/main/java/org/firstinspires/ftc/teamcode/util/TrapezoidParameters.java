package org.firstinspires.ftc.teamcode.util;

/**
 * 台形速度プロファイルのパラメータを保持するクラス。
 *
 * <p>速度・加速度の制限と、出力値の範囲を定義します。</p>
 */
public class TrapezoidParameters {
    /**
     * 最大速度 [単位/秒]
     */
    public final double maxVel;

    /**
     * 最大加速度 [単位/秒²]
     */
    public final double maxAccel;

    /**
     * 時間差分の上限（ループ遅延対策）[秒]
     */
    public final double maxDt;

    /**
     * 出力の最小値
     */
    public final double minValue;

    /**
     * 出力の最大値
     */
    public final double maxValue;

    /**
     * パラメータを作成します。
     *
     * @param maxVel   最大速度 [単位/秒]
     * @param maxAccel 最大加速度 [単位/秒²]
     * @param maxDt    時間差分の上限 [秒]
     * @param minValue 出力の最小値
     * @param maxValue 出力の最大値
     */
    public TrapezoidParameters(double maxVel, double maxAccel, double maxDt, double minValue, double maxValue) {
        this.maxVel = Math.abs(maxVel);
        this.maxAccel = Math.abs(maxAccel);
        this.maxDt = maxDt;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}