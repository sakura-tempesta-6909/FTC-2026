package org.firstinspires.ftc.teamcode.opmode;

import com.pedropathing.geometry.Pose;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.Motif;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

/**
 * 全 OpMode 共通の基底クラス。
 * Pedro + 全 Subsystem + BulkRead + Bindings の登録と Drawing の初期化を一括で行う。
 * Driver Hub テレメトリの出力も共通化する。
 */
public abstract class FTCBaseOpMode extends NextFTCOpMode {

    /**
     * Driver Station (Driver Hub) に表示するための FTC SDK 標準テレメトリ。
     * onInit() で telemetry が Panels ラッパーに置き換えられる前に保存する。
     */
    protected Telemetry driverStationTelemetry;

    public FTCBaseOpMode() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(
                        ShooterSubsystem.INSTANCE,
                        FeederSubsystem.INSTANCE,
                        IntakeSubsystem.INSTANCE,
                        LimelightSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    @Override
    public void onInit() {
        // Panels ラッパーに置き換える前に、元の FTC SDK テレメトリを保存
        driverStationTelemetry = telemetry;
        Drawing.init();
    }

    /**
     * Driver Hub 画面に厳選した情報を表示する。
     * 各 OpMode の onUpdate() から呼び出す。
     * FTCBaseOpMode を継承しない OpMode からも使えるよう static にしている。
     *
     * @param driverHubTelemetry OpMode の telemetry (FTC SDK の Telemetry)
     */
    public static void updateDriverHubTelemetry(Telemetry driverHubTelemetry) {
        // --- MOTIF ---
        Motif voted = LimelightSubsystem.INSTANCE.getVotedMotif();
        if (voted != null) {
            driverHubTelemetry.addData("MOTIF",
                    String.format("%s (確度:%.0f%%)",
                            voted.getMotifName(),
                            LimelightSubsystem.INSTANCE.getDetectionHistory().getConfidence() * 100));
            driverHubTelemetry.addData("パターン", voted.getPatternString());
        } else {
            int count = LimelightSubsystem.INSTANCE.getDetectionHistory().getObeliskDetectionCount();
            driverHubTelemetry.addData("MOTIF", count == 0 ? "未検出" : String.format("検出中...(%d回)", count));
        }

        // --- 射出 ---
        driverHubTelemetry.addData("射出", ShooterSubsystem.INSTANCE.getStatusText());

        // --- 照準 ---
        double distance = LimelightSubsystem.INSTANCE.getDistance();
        if (distance > 0) {
            driverHubTelemetry.addData("距離", String.format("%.0fcm  tx:%.1f°", distance, LimelightSubsystem.INSTANCE.getTx()));
        } else {
            driverHubTelemetry.addData("距離", "タグ未検出");
        }

        // --- 位置 ---
        Pose odoPose = PedroComponent.follower().getPose();
        driverHubTelemetry.addData("Odo",
                String.format("(%.1f, %.1f) h:%.1f°",
                        odoPose.getX(), odoPose.getY(),
                        Math.toDegrees(odoPose.getHeading())));
        Pose llPose = LimelightSubsystem.INSTANCE.getLimelightPose();
        if (llPose != null) {
            driverHubTelemetry.addData("LL",
                    String.format("(%.1f, %.1f) h:%.1f°",
                            llPose.getX(), llPose.getY(),
                            Math.toDegrees(llPose.getHeading())));
        } else {
            driverHubTelemetry.addData("LL", "---");
        }

        // --- 実行中コマンド ---
        String running = String.join(", ", CommandManager.INSTANCE.snapshot());
        driverHubTelemetry.addData("実行中", running.isEmpty() ? "---" : running);

        driverHubTelemetry.update();
    }
}
