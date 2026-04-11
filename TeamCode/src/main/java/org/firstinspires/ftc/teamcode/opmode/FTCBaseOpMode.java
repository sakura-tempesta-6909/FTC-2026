package org.firstinspires.ftc.teamcode.opmode;

import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.lib.Drawing;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

/**
 * 全 OpMode 共通の基底クラス。
 * Pedro + 全 Subsystem + BulkRead + Bindings の登録と Drawing の初期化を一括で行う。
 */
public abstract class FTCBaseOpMode extends NextFTCOpMode {

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
        Drawing.init();
    }
}
