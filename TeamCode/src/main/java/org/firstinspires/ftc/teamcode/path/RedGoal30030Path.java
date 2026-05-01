package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoal30030Path {
    public PathChain Path1, Path2, Path3;

    public RedGoal30030Path(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(112.067, 135.348),

                                new Pose(92.980, 135.530)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(92.980, 135.530),

                                new Pose(113.944, 117.513)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(113.944, 117.513),

                                new Pose(102.725, 128.672)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(41))

                .build();
    }
}

