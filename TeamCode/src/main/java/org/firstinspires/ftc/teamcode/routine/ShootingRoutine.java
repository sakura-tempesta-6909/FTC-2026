package org.firstinspires.ftc.teamcode.routine;

import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import org.firstinspires.ftc.teamcode.command.FeederCommand;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.config.Const;

/**
 * Shooter + Feeder + Intake を組み合わせた射出ルーチン。
 *
 * <p>連射フェーズを {@link ParallelGroup} で組み、各 leaf が永続コマンドであるため、
 * Routine をいつキャンセルしても全 Subsystem が setStop で停止する。
 *
 * <p>requires (自動集約): Shooter, Feeder, Intake
 */
public class ShootingRoutine {

    private ShootingRoutine() {}

    /**
     * 引き戻し → スピンアップ → 連射。
     * <ol>
     *   <li>retractFor — Feeder を {RETRACT_DURATION_SECONDS} 秒だけ引き戻して自動停止</li>
     *   <li>spinUp — Shooter が目標 RPM に達するまで待機 (条件完了)</li>
     *   <li>ParallelGroup(holdRpm, intake, feed) — 全永続、cancel で全停止</li>
     * </ol>
     * <p>終了: 永続 (最終フェーズの ParallelGroup が終わらない) / 中断時: 全 Subsystem 停止
     */
    public static Command shootWithRetract() {
        return new SequentialGroup(
                // フィーダー引き戻し + シューター逆回転を同時に行い、詰まりを解消
                new ParallelDeadlineGroup(
                        new Delay(Const.ShootingRoutine.RETRACT_DURATION_SECONDS),
                        IntakeCommand.intake(),
                        FeederCommand.retract(),
                        ShooterCommand.spinUpReverse()
                ),
                ShooterCommand.spinUp(),
                new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        IntakeCommand.slowIntake(),
                        FeederCommand.feed()
                )
        ).named("shootWithRetract");
    }

    public static Command shootWithRetract1280RPM() {
        return new SequentialGroup(
                new ParallelDeadlineGroup(
                        new Delay(Const.ShootingRoutine.RETRACT_DURATION_SECONDS),
                        IntakeCommand.intake(),
                        FeederCommand.retract(),
                        ShooterCommand.spinUpReverse()
                ),
                ShooterCommand.spinUpFor1280RPM(),
                new ParallelGroup(
                       ShooterCommand.holdRpm(),
                       IntakeCommand.slowIntake(),
                        FeederCommand.feed()
        )
        ).named("shootwithRetract1280RPM");
    }
    /**
     * スピンアップ → 連射 (引き戻しなし)。
     * <ol>
     *   <li>spinUp — Shooter が目標 RPM に達するまで待機</li>
     *   <li>ParallelGroup(holdRpm, intake, feed) — 全永続、cancel で全停止</li>
     * </ol>
     * <p>終了: 永続 / 中断時: 全 Subsystem 停止
     */
    public static Command shootContinuous() {
        return new SequentialGroup(
                FeederCommand.retractFor(Const.ShootingRoutine.RETRACT_DURATION_SECONDS),
                ShooterCommand.spinUp(),
                new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        IntakeCommand.intake(),
                        FeederCommand.feed()
                )
        ).named("shootContinuous");
    }

    /**
     * 連射 (Intake 非所有版)。Intake の制御は呼び出し側に任せる。
     * <p>{@link #shootContinuous()} との差分は内部で Intake を使わないこと。
     * ParallelDeadlineGroup の兄弟に {@link IntakeCommand#slowIntake()} 等を置いて
     * 射撃中の Intake を外部制御したい場合に使う。
     * <p>requires (自動集約): Shooter, Feeder
     */
    public static Command shootContinuousNoIntake() {
        return new SequentialGroup(
                FeederCommand.retractFor(Const.ShootingRoutine.RETRACT_DURATION_SECONDS),
                ShooterCommand.spinUp(),
                new ParallelGroup(
                        ShooterCommand.holdRpm(),
                        FeederCommand.feed()
                )
        ).named("shootContinuousNoIntake");
    }
}
