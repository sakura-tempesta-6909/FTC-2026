package org.firstinspires.ftc.teamcode.subsystem.io;

import Ori.Coval.Logging.AutoLog;

/**
 * Subsystem に属さないシステム全般の状態スナップショット。
 * バッテリー電圧、ループ時間、実行中コマンド等、OpMode 単位で変化する値を保持。
 * {@code @AutoLog} によりコンパイル時に {@code SystemInputsAutoLogged} が生成され、
 * {@code AutoLogManager.periodic()} で wpilog に自動記録される。
 */
@AutoLog
public class SystemInputs {
    /**
     * ロボットのバッテリー電圧 (V)。低下でモーター性能劣化の指標。
     */
    public double batteryVoltage;

    /**
     * 直前ループの所要時間 (ms)。20ms 前後が正常、大きく超えたら処理過多。
     */
    public double loopMs;

    /**
     * 現在実行中のコマンド名を "|" 区切りで連結したもの。状態の時系列把握用。
     */
    public String runningCmds = "";
}
