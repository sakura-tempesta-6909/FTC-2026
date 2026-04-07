package org.firstinspires.ftc.teamcode.command;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;

/**
 * {@link FeederSubsystem} だけを操作する単一責務コマンド集。
 * <p>
 * 全コマンドは「永続実行 + setStop で停止」パターン。中断時に必ず Feeder が
 * STOP に戻るため、競合検出ベースの自動キャンセルだけで安全に止まる。
 */
public class FeederCommand {

    private FeederCommand() {
    }

    /**
     * 通常の送り込み速度で前進し続ける。中断で停止。
     */
    public static Command feed() {
        return new LambdaCommand()
                .setStart(FeederSubsystem.INSTANCE::feed)
                .setIsDone(() -> false)
                .setStop(interrupted -> FeederSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(FeederSubsystem.INSTANCE)
                .named("feed");
    }

    /**
     * 弱い送り込み速度で前進し続ける (インテーク中の待機など)。中断で停止。
     */
    public static Command weakFeed() {
        return new LambdaCommand()
                .setStart(FeederSubsystem.INSTANCE::weakFeed)
                .setIsDone(() -> false)
                .setStop(interrupted -> FeederSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(FeederSubsystem.INSTANCE)
                .named("weakFeed");
    }

    /**
     * 引き戻し方向に逆転し続ける。中断で停止。
     */
    public static Command retract() {
        return new LambdaCommand()
                .setStart(FeederSubsystem.INSTANCE::retract)
                .setIsDone(() -> false)
                .setStop(interrupted -> FeederSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(FeederSubsystem.INSTANCE)
                .named("retract");
    }
}
