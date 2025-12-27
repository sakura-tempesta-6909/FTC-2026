package org.firstinspires.ftc.teamcode.util;

public class TrapezoidParameters {
    public final double maxVel;    // [unit/sec]
    public final double maxAccel;  // [unit/sec^2]
    public final double maxDt;     // dtの上限（ループ飛び対策）
    public final double minValue;  // 出力の最小値
    public final double maxValue;  // 出力の最大値

    public TrapezoidParameters(double maxVel, double maxAccel, double maxDt, double minValue, double maxValue) {
        this.maxVel = Math.abs(maxVel);
        this.maxAccel = Math.abs(maxAccel);
        this.maxDt = maxDt;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }
}