package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * Feeder モーターの診断用スナップショット。
 * ボール検知の候補値として currentA が重要 (ボール接触で負荷増→電流増の想定)。
 */
@AutoLog
public class FeederInputs {
    /**
     * コマンドパワー。FEED=0.7, WEAK_FEED=0.18, RETRACT=-1.0 等。
     */
    public double power;

    /**
     * モーターに実効的にかかる電圧 (V)。batteryVoltage × power で算出。
     */
    public double voltageV;

    /**
     * モーター電流 (A)。ボール接触時のスパイク検出用。
     */
    public double currentA;
}
