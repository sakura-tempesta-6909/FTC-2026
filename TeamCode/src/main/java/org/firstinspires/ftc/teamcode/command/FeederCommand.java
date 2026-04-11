package org.firstinspires.ftc.teamcode.command;

import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;

/**
 * {@link FeederSubsystem} のみを操作するコマンド集。
 *
 * <ul>
 *   <li>永続コマンド ({@link #feed}, {@link #weakFeed}, {@link #retract})
 *       — 外部からの cancel / 競合検出でのみ終了し、setStop で Feeder を停止する。</li>
 *   <li>時限コマンド ({@link #retractFor})
 *       — 指定秒数で自動終了。endAfter の代替 (NextFTC ParallelRaceGroup バグ回避)。</li>
 * </ul>
 */
public class FeederCommand {

    private FeederCommand() {}

    /**
     * 通常速度で送り込みを続ける。
     * <p>終了: 永続 (cancel のみ) / 中断時: Feeder 停止 / requires: Feeder
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
     * 弱い速度で送り込みを続ける (インテーク中の詰まり防止用)。
     * <p>終了: 永続 (cancel のみ) / 中断時: Feeder 停止 / requires: Feeder
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
     * 引き戻し方向に逆転し続ける。
     * <p>終了: 永続 (cancel のみ) / 中断時: Feeder 停止 / requires: Feeder
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

    /**
     * 指定秒数だけ引き戻して自動停止する。
     * <p>終了: 経過時間 ≥ seconds / 中断時: Feeder 停止 / requires: Feeder
     * <p>注: {@code retract().endAfter()} は NextFTC の ParallelRaceGroup バグで
     * 永続コマンドに効かないため、自前タイマーで代替している。
     *
     * @param seconds 引き戻し時間 (秒)
     */
    public static Command retractFor(double seconds) {
        ElapsedTime timer = new ElapsedTime();
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.retract();
                    timer.reset();
                })
                .setIsDone(() -> timer.seconds() >= seconds)
                .setStop(interrupted -> FeederSubsystem.INSTANCE.stop())
                .setInterruptible(true)
                .addRequirements(FeederSubsystem.INSTANCE)
                .named("retractFor");
    }
}
