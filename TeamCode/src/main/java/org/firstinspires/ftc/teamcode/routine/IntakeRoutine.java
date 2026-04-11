package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import org.firstinspires.ftc.teamcode.command.FeederCommand;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;

/**
 * Intake + Feeder を同時に扱うルーチン。
 * 詰まり防止のため、インテーク中にフィーダーを弱く回す等の組み合わせを表現する。
 *
 * <p>requires (自動集約): Intake, Feeder
 */
public class IntakeRoutine {

    private IntakeRoutine() {}

    /**
     * 取り込み + フィーダー弱送り を並列で実行。
     * <p>終了: 永続 (cancel のみ) / 中断時: Intake, Feeder 両方停止
     */
    public static Command intakeWithWeakFeed() {
        return new ParallelGroup(
                IntakeCommand.intake(),
                FeederCommand.weakFeed()
        ).named("intakeWithWeakFeed");
    }

    /**
     * 排出 + フィーダー引き戻し を並列で実行。
     * <p>終了: 永続 (cancel のみ) / 中断時: Intake, Feeder 両方停止
     */
    public static Command outtakeWithRetract() {
        return new ParallelGroup(
                IntakeCommand.outtake(),
                FeederCommand.retract()
        ).named("outtakeWithRetract");
    }
}
