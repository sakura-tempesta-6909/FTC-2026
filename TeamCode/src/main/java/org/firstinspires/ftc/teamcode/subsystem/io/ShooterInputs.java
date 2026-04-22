package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * Shooter モーターの診断用スナップショット。
 * PID 挙動とバッテリー消費の相関分析に使う。
 * postToFtcDashboard=true で AScope-Lite の live view / FTC Dashboard にも流す。
 */
@AutoLog
public class ShooterInputs {
    /**
     * コマンドパワー (-1.0 〜 1.0)。PID の出力結果。
     */
    public double power;

    /**
     * モーターに実効的にかかる電圧 (V)。batteryVoltage × power で算出。
     */
    public double voltageV;

    /**
     * モーター電流 (A)。加速時・HOLD PID 振動時のピーク検出用。
     */
    public double currentA;

    /**
     * エンコーダ速度 (encoder ticks / second, getVelocity 値)。
     */
    public double velocity;
}
