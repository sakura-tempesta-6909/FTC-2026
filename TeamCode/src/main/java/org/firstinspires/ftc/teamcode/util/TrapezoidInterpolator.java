package org.firstinspires.ftc.teamcode.util;

import dev.nextftc.control.KineticState;
import dev.nextftc.control.interpolators.InterpolatorElement;

public class TrapezoidInterpolator implements InterpolatorElement {
    private final TrapezoidParameters params;

    private KineticState goal;
    private double position;
    private double velocity;
    private double acceleration;
    private long lastTimeNanos = System.nanoTime();

    public TrapezoidInterpolator(TrapezoidParameters params, double startPosition) {
        this.params = params;
        this.position = startPosition;
        this.velocity = 0.0;
        this.acceleration = 0.0;
        this.goal = new KineticState(startPosition, 0.0, 0.0);
    }

    // ========== 目標設定 ==========

    @Override
    public void setGoal(KineticState goal) {
        double clampedPosition = clamp(goal.getPosition(), params.minValue, params.maxValue);
        this.goal = new KineticState(clampedPosition, goal.getVelocity(), goal.getAcceleration());
    }

    public void setGoal(double targetPosition) {
        setGoal(new KineticState(targetPosition, 0.0, 0.0));
    }

    // ========== 状態取得 ==========

    @Override
    public KineticState getGoal() {
        return goal;
    }

    public double getGoalPosition() {
        return goal.getPosition();
    }

    @Override
    public KineticState getCurrentReference() {
        step();
        return new KineticState(position, velocity, acceleration);
    }

    public double getPosition() {
        step();
        return clamp(position, params.minValue, params.maxValue);
    }

    // ========== リセット ==========

    @Override
    public void reset() {
        position = goal.getPosition();
        stopMotion();
        lastTimeNanos = System.nanoTime();
    }

    // ========== 内部処理 ==========
    //
    // 台形速度プロファイル:
    //   1. 目標に到達済み → 停止
    //   2. 速度が逆方向 → 全力で減速
    //   3. 制動距離内 → 減速開始
    //   4. それ以外 → 加速
    //
    // 制動距離 = v² / (2 × maxAccel)
    //

    private void step() {
        double deltaTime = calculateDeltaTime();
        if (deltaTime <= 0) return;

        double goalPosition = goal.getPosition();
        double error = goalPosition - position;

        if (isAtGoal(error)) {
            arriveAtGoal(goalPosition);
            return;
        }

        double targetDirection = Math.signum(error);
        double newAcceleration = calculateAcceleration(error, targetDirection);
        double newVelocity = clamp(velocity + newAcceleration * deltaTime, -params.maxVel, params.maxVel);
        double newPosition = position + 0.5 * (velocity + newVelocity) * deltaTime;

        if (hasOvershot(goalPosition, newPosition, targetDirection)) {
            arriveAtGoal(goalPosition);
        } else {
            position = newPosition;
            velocity = newVelocity;
            acceleration = newAcceleration;
        }
    }

    private double calculateDeltaTime() {
        long now = System.nanoTime();
        double deltaTime = (now - lastTimeNanos) / 1e9;
        lastTimeNanos = now;
        return Math.min(deltaTime, params.maxDt);
    }

    private boolean isAtGoal(double error) {
        boolean isClose = Math.abs(error) < 0.01 && Math.abs(velocity) < 0.1;
        boolean isVeryClose = Math.abs(error) < 0.001;
        return isClose || isVeryClose;
    }

    private double calculateAcceleration(double error, double targetDirection) {
        double velocityDirection = Math.signum(velocity);

        // 速度が目標と逆方向 → 速度を反転させる
        if (velocityDirection != 0 && velocityDirection != targetDirection) {
            return targetDirection * params.maxAccel;
        }

        // 速度が目標方向（または停止中）→ 制動距離で判定
        double brakingDistance = (velocity * velocity) / (2.0 * params.maxAccel);
        boolean shouldDecelerate = Math.abs(error) <= brakingDistance;

        return shouldDecelerate
                ? -targetDirection * params.maxAccel
                : targetDirection * params.maxAccel;
    }

    private boolean hasOvershot(double goalPosition, double newPosition, double targetDirection) {
        return targetDirection != 0.0 && Math.signum(goalPosition - newPosition) != targetDirection;
    }

    private void arriveAtGoal(double goalPosition) {
        position = goalPosition;
        stopMotion();
    }

    private void stopMotion() {
        velocity = 0.0;
        acceleration = 0.0;
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
