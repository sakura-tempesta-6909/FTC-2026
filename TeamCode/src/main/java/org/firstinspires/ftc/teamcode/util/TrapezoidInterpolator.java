package org.firstinspires.ftc.teamcode.util;

import dev.nextftc.control.KineticState;
import dev.nextftc.control.interpolators.InterpolatorElement;

public class TrapezoidInterpolator implements InterpolatorElement {
    private KineticState goal = new KineticState(0.0, 0.0, 0.0);

    private double x;
    private double v;
    private double a;

    private final TrapezoidParameters p;
    private long lastNanos = System.nanoTime();

    public TrapezoidInterpolator(TrapezoidParameters params, double startPos) {
        this.p = params;
        this.x = startPos;
        this.v = 0.0;
        this.a = 0.0;
        this.goal = new KineticState(startPos, 0.0, 0.0);
    }

    @Override
    public KineticState getGoal() {
        return goal;
    }

    @Override
    public void setGoal(KineticState goal) {
        this.goal = goal;
    }

    @Override
    public KineticState getCurrentReference() {
        step();
        return new KineticState(x, v, a);
    }

    @Override
    public void reset() {
        x = goal.getPosition();
        v = 0.0;
        a = 0.0;
        lastNanos = System.nanoTime();
    }

    private void step() {
        long now = System.nanoTime();
        double dt = (now - lastNanos) / 1e9;
        lastNanos = now;

        if (dt <= 0) return;
        if (dt > p.maxDt) dt = p.maxDt;

        double gx = goal.getPosition();
        double error = gx - x;

        // 目標に十分近い場合は即座に停止
        // (位置が近い AND 速度が小さい) OR (位置が非常に近い)
        if ((Math.abs(error) < 0.01 && Math.abs(v) < 0.1) || Math.abs(error) < 0.001) {
            x = gx;
            v = 0;
            a = 0;
            return;
        }

        double dir = Math.signum(error);  // 目標の方向 (+1 or -1)
        double velDir = Math.signum(v);   // 現在の速度の方向

        double accel;

        // ケース1: 速度が目標と逆方向 → 無条件で速度を0に向けて加速
        if (velDir != 0 && velDir != dir) {
            // 速度を反転させる方向に最大加速度
            accel = dir * p.maxAccel;
        }
        // ケース2: 速度が目標方向と同じ（または停止中）→ 制動距離で判定
        else {
            // 今の速度で止まるのに必要な距離
            double brakingDist = (v * v) / (2.0 * p.maxAccel);

            if (Math.abs(error) <= brakingDist) {
                // 制動距離内 → 減速
                accel = -dir * p.maxAccel;
            } else {
                // まだ遠い → 加速
                accel = dir * p.maxAccel;
            }
        }

        // 速度を更新（最大速度でクランプ）
        double newV = clamp(v + accel * dt, -p.maxVel, p.maxVel);

        // 位置を更新（平均速度を使用してより正確に）
        double newX = x + 0.5 * (v + newV) * dt;

        // オーバーシュート防止
        if (dir != 0.0 && Math.signum(gx - newX) != dir) {
            x = gx;
            v = 0;
            a = 0;
        } else {
            x = newX;
            v = newV;
            a = accel;
        }
    }

    private static double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }
}
