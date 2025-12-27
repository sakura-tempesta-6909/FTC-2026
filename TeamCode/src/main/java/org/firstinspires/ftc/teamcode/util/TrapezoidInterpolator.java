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

        if (Math.abs(error) < 1e-4 && Math.abs(v) < 1e-4) {
            x = gx;
            v = 0;
            a = 0;
            return;
        }

        double dir = Math.signum(error);

        // 今の速度で止まるのに必要な距離
        double brakingDist = (v * v) / (2.0 * p.maxAccel);

        // 近いなら減速、遠いなら加速
        double accel = (Math.abs(error) <= brakingDist)
                ? -Math.signum(v) * p.maxAccel
                : dir * p.maxAccel;

        double newV = clamp(v + accel * dt, -p.maxVel, p.maxVel);
        double newX = x + newV * dt;

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
