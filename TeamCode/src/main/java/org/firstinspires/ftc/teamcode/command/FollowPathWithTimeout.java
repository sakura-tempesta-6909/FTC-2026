package org.firstinspires.ftc.teamcode.command;

import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.utility.LambdaCommand;
import dev.nextftc.extensions.pedro.FollowPath;

/**
 * タイムアウト付き FollowPath。
 * パスが完了するか、タイムアウトに達するか、早い方で終了する。
 */
public class FollowPathWithTimeout {

    private FollowPathWithTimeout() {}

    /**
     * @param path           追従するパス
     * @param holdEnd        終端で位置を保持するか
     * @param maxSpeed       最大速度 (0〜1)
     * @param timeoutSeconds タイムアウト秒数
     */
    public static Command create(PathChain path, boolean holdEnd, double maxSpeed, double timeoutSeconds) {
        ElapsedTime timer = new ElapsedTime();
        FollowPath followPath = new FollowPath(path, holdEnd, maxSpeed);
        return new LambdaCommand()
                .setStart(() -> {
                    timer.reset();
                    followPath.start();
                })
                .setUpdate(followPath::update)
                .setIsDone(() -> followPath.isDone() || timer.seconds() >= timeoutSeconds)
                .setStop(followPath::stop)
                .named("FollowPathWithTimeout");
    }
}
