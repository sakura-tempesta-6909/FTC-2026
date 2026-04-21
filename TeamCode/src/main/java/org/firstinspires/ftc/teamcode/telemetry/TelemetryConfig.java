package org.firstinspires.ftc.teamcode.telemetry;

import com.bylazar.configurables.annotations.Configurable;

/**
 * 3 つの出力先 (DriverHub / Panels / KoalaLog) を Panels UI から live 切替するための設定。
 * 試合中に DriverHub 以外を切るなど、状況に応じた柔軟な制御が目的。
 */
@Configurable
public class TelemetryConfig {
    public static boolean ENABLE_DRIVER_HUB = true;
    public static boolean ENABLE_PANELS = true;
    public static boolean ENABLE_KOALA_LOG = true;
}
