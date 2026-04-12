package org.firstinspires.ftc.teamcode.command;

import com.pedropathing.geometry.Pose;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.extensions.pedro.PedroComponent;
import org.firstinspires.ftc.teamcode.subsystem.LimelightSubsystem;

/**
 * Limelight の推定位置でオドメトリを補正するコマンド。
 * <p>
 * タグが検出されていれば {@code follower.setPose(limelightPose)} で全補正し、
 * 検出されていなければ何もせず終了する。
 * Auto のパス間に挟んで使用する。
 */
public class CorrectionCommand {

    private CorrectionCommand() {
    }

    /**
     * Limelight 推定位置でオドメトリを即座に補正する。
     * タグ未検出またはフィールド外の場合は何もしない。
     */
    public static Command correct() {
        return new LambdaCommand()
                .setStart(() -> {
                    Pose limelightPose = LimelightSubsystem.INSTANCE.getLimelightPose();
                    if (limelightPose != null && isOnField(limelightPose)) {
                        PedroComponent.follower().setPose(limelightPose);
                    }
                })
                .setIsDone(() -> true)
                .named("LimelightCorrection");
    }

    /**
     * 位置がフィールド内 (0〜144 インチ) に収まっているか判定する。
     */
    private static boolean isOnField(Pose pose) {
        double margin = 10;
        return pose.getX() >= -margin && pose.getX() <= 144 + margin
                && pose.getY() >= -margin && pose.getY() <= 144 + margin;
    }
}
