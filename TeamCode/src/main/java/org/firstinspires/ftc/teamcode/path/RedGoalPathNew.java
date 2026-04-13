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
    public PathChain Path5;
    public PathChain Path6;
    public PathChain Path7;
    public PathChain Path8;

    public RedGoalPathNew(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(119.138, 135.398),

                                new Pose(110.000, 106.889)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.000, 106.889),

                                new Pose(97.043, 82.816)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(97.043, 82.816),

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

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(101.327, 106.889),

                                new Pose(101.912, 59.310)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(101.912, 59.310),

                                new Pose(134.885, 58.823)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(134.885, 58.823),

                                new Pose(125.204, 58.619)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(125.204, 58.619),

                                new Pose(101.327, 106.889)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();
    }
}
