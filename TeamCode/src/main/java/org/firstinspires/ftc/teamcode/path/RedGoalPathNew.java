package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPathNew {
    public PathChain Path1;
    public PathChain Path2;
    public PathChain Path3;
    public PathChain Path4;

    public RedGoalPathNew(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(121.779, 124.106),

                                new Pose(101.249, 106.889)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(40))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(101.249, 106.889),

                                new Pose(101.327, 83.044)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();
        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(101.327, 83.044),

                                new Pose(128.115, 83.044)
                        )
                ).setTangentHeadingInterpolation()

                .build();
        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(128.115, 83.044),

                                new Pose(101.327, 106.889)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();
    }
}