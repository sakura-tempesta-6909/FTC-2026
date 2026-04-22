package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * Intake モーターの診断用スナップショット。
 * Auto 全編 slowIntake (0.3) が走る区間での累積消費を見る用途。
 */
@AutoLog
public class IntakeInputs {
    /**
     * コマンドパワー。INTAKE=1.0, SLOW_INTAKE=0.3, REVERSE=-1.0 等。
     */
    public double power;

    /**
     * モーターに実効的にかかる電圧 (V)。batteryVoltage × power で算出。
     */
    public double voltageV;

    /**
     * モーター電流 (A)。ストール検知にも利用可能。
     */
    public double currentA;
}
