package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import org.firstinspires.ftc.teamcode.command.FeederCommand;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;

/**
 * インテーク系の組み合わせルーチン。
 * <p>
 * インテーク中に弾の詰まりを防ぐためフィーダーを弱く回す、などの
 * 「Intake と Feeder を同時に扱いたい」用途を ParallelGroup で表現する。
 */
public class IntakeRoutine {

    private IntakeRoutine() {
    }

    /**
     * 取り込み中フィーダーも弱く前進させる。
     * 中断時は ParallelGroup が中の両 leaf の setStop を呼び、両方とも停止する。
     */
    public static Command intakeWithWeakFeed() {
        return new ParallelGroup(
                IntakeCommand.intake(),
                FeederCommand.weakFeed()
        ).named("intakeWithWeakFeed");
    }

    /**
     * 排出 (逆転) 中フィーダーも引き戻し方向に逆転させる。
     */
    public static Command outtakeWithRetract() {
        return new ParallelGroup(
                IntakeCommand.outtake(),
                FeederCommand.retract()
        ).named("outtakeWithRetract");
    }
}
