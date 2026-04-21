package org.firstinspires.ftc.teamcode.telemetry;

import Ori.Coval.Logging.AutoLogManager;
import com.qualcomm.robotcore.hardware.HardwareMap;
import dev.nextftc.core.commands.CommandManager;
import org.firstinspires.ftc.teamcode.subsystem.io.SystemInputsAutoLogged;

/**
 * KoalaLog への出力を担当する Publisher。
 * <p>
 * 各 Subsystem が自身の {@code *InputsAutoLogged} を持つ設計なので、
 * この Publisher の責務は以下 2 つに絞られる:
 * <ol>
 *   <li>Subsystem に属さない {@link SystemInputsAutoLogged} を埋める</li>
 *   <li>{@link AutoLogManager#periodic()} を呼び、全登録済み {@code Logged} を一括 wpilog 出力</li>
 * </ol>
 */
public class KoalaLogPublisher {
    /**
     * Subsystem 非依存の状態。バッテリー / ループ / 実行中コマンド等。
     */
    public static final SystemInputsAutoLogged systemInputs = new SystemInputsAutoLogged();

    private KoalaLogPublisher() {
    }

    public static void update(HardwareMap hw, double loopSeconds) {
        if (!TelemetryConfig.ENABLE_KOALA_LOG) return;

        systemInputs.batteryVoltage = hw.voltageSensor.iterator().next().getVoltage();
        systemInputs.loopMs = loopSeconds * 1000.0;
        systemInputs.runningCmds = String.join("|", CommandManager.INSTANCE.snapshot());

        // 全 @AutoLog 登録済みクラスの toLog() を呼ぶ → wpilog に書き込む
        AutoLogManager.periodic();
    }
}
