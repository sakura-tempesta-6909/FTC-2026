package org.firstinspires.ftc.teamcode.util;

public class TrapezoidParameters {
    public final double maxVel;    // [unit/sec]
    public final double maxAccel;  // [unit/sec^2]
    public final double maxDt;     // dtの上限（ループ飛び対策）

    public TrapezoidParameters(double maxVel, double maxAccel, double maxDt) {
        this.maxVel = Math.abs(maxVel);
        this.maxAccel = Math.abs(maxAccel);
        this.maxDt = maxDt;
    }
}