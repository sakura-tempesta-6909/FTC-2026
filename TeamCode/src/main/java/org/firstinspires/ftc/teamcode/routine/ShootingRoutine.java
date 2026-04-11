package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import org.firstinspires.ftc.teamcode.command.FeederCommand;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * 複数 Subsystem を組み合わせた「射出ルーチン」。
 * <p>
 * 各単一責務 Command (ShooterCommand / FeederCommand / IntakeCommand) を
 * SequentialGroup と ParallelGroup で組み立てるだけの宣言的レイヤ。
 * <p>
 * 設計のキモは <strong>連射フェーズを ParallelGroup で組む</strong> こと。
 * Shooter を {@link ShooterCommand#holdRpm()} で永続管理しているため、
 * Routine がいつキャンセルされても ParallelGroup が中の全 leaf の
 * {@code setStop} を呼んで Shooter / Feeder / Intake を全て停止する。
 */
public class ShootingRoutine {

    private ShootingRoutine() {
    }

    /**
     * 引き戻し付き射出ルーチン。
     * <ol>
     *   <li>フィーダーを {@link Const.ShootingRoutine#RETRACT_DURATION_SECONDS} 秒だけ引き戻す</li>
     *   <li>シューターを目標 RPM まで加速 (距離ベースで自動決定)</li>
     *   <li>連射: holdRpm + intake + feed を並列で永続実行</li>
     * </ol>
     */
    public static Command shootWithRetract() {
        return new SequentialGroup(
                FeederCommand.retractFor(Const.ShootingRoutine.RETRACT_DURATION_SECONDS),
                ShooterCommand.spinUp(),
                new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        IntakeCommand.intake(),
                        FeederCommand.feed()
                )
        ).named("shootWithRetract");
    }

    /**
     * 引き戻しなしの射出ルーチン。
     * <ol>
     *   <li>シューターを目標 RPM まで加速</li>
     *   <li>連射: holdRpm + intake + feed を並列で永続実行</li>
     * </ol>
     */
    public static Command shootContinuous() {
        return new SequentialGroup(
                ShooterCommand.spinUp(),
                new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        IntakeCommand.intake(),
                        FeederCommand.feed()
                )
        ).named("shootContinuous");
    }
}
