package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * ドライブ 4 モーターの診断用スナップショット。
 * パス追従中のピーク電流 (spinUpForPath と同時に発生する区間) を特定する用途。
 */
@AutoLog
public class DriveInputs {
    // --- コマンドパワー ---
    public double leftFrontPower;
    public double rightFrontPower;
    public double leftRearPower;
    public double rightRearPower;

    // --- モーター電圧 (V) = batteryVoltage × power ---
    public double leftFrontVoltageV;
    public double rightFrontVoltageV;
    public double leftRearVoltageV;
    public double rightRearVoltageV;

    // --- モーター電流 (A) ---
    public double leftFrontCurrentA;
    public double rightFrontCurrentA;
    public double leftRearCurrentA;
    public double rightRearCurrentA;

    // --- エンコーダ速度 ---
    public double leftFrontVelocity;
    public double rightFrontVelocity;
    public double leftRearVelocity;
    public double rightRearVelocity;
}
