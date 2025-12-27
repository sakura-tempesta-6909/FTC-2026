package org.firstinspires.ftc.teamcode.util;

import dev.nextftc.control.KineticState;
import dev.nextftc.control.interpolators.InterpolatorElement;

/**
 * 台形速度プロファイルを用いた補間器。
 *
 * <p>目標値に向かって、加速→定速→減速の滑らかな動きを生成します。
 * モーターパワーなどを急激に変化させず、機械に優しい制御を実現します。</p>
 *
 * <h3>使用例:</h3>
 * <pre>{@code
 * TrapezoidInterpolator profile = new TrapezoidInterpolator(
 *     new TrapezoidParameters(2.0, 10.0, 0.05, -1.0, 1.0),
 *     0.0
 * );
 * profile.setGoal(1.0);           // 目標を設定
 * double current = profile.getPosition();  // 現在の補間値を取得
 * }</pre>
 */
public class TrapezoidInterpolator implements InterpolatorElement {
    private final TrapezoidParameters params;

    private KineticState goal;
    private double position;
    private double velocity;
    private double acceleration;
    private long lastTimeNanos = System.nanoTime();

    /**
     * 補間器を作成します。
     *
     * @param params        台形プロファイルのパラメータ
     * @param startPosition 初期位置
     */
    public TrapezoidInterpolator(TrapezoidParameters params, double startPosition) {
        this.params = params;
        this.position = startPosition;
        this.velocity = 0.0;
        this.acceleration = 0.0;
        this.goal = new KineticState(startPosition, 0.0, 0.0);
    }

    // ========== 目標設定 ==========

    /**
     * 目標状態を設定します。位置は自動的に範囲内にクランプされます。
     *
     * @param goal 目標状態（位置・速度・加速度）
     */
    @Override
    public void setGoal(KineticState goal) {
        double clampedPosition = clamp(goal.getPosition(), params.minValue, params.maxValue);
        this.goal = new KineticState(clampedPosition, goal.getVelocity(), goal.getAcceleration());
    }

    /**
     * 目標位置を設定します（速度・加速度は0）。
     *
     * @param targetPosition 目標位置
     */
    public void setGoal(double targetPosition) {
        setGoal(new KineticState(targetPosition, 0.0, 0.0));
    }

    // ========== 状態取得 ==========

    /**
     * 現在の目標状態を取得します。
     *
     * @return 目標状態
     */
    @Override
    public KineticState getGoal() {
        return goal;
    }

    /**
     * 現在の目標位置を取得します。
     *
     * @return 目標位置
     */
    public double getGoalPosition() {
        return goal.getPosition();
    }

    /**
     * 現在の参照状態を取得します（位置・速度・加速度）。
     * 呼び出すたびに内部状態が更新されます。
     *
     * @return 現在の参照状態
     */
    @Override
    public KineticState getCurrentReference() {
        step();
        return new KineticState(position, velocity, acceleration);
    }

    /**
     * 現在の位置を取得します（範囲内にクランプ済み）。
     * 呼び出すたびに内部状態が更新されます。
     *
     * @return 現在の位置
     */
    public double getPosition() {
        step();
        return clamp(position, params.minValue, params.maxValue);
    }

    // ========== リセット ==========

    /**
     * 現在位置を目標位置に即座に移動し、速度・加速度を0にリセットします。
     */
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
