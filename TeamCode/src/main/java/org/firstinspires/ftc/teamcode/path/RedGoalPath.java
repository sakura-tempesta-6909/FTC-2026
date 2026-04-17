package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8;

    public RedGoalPath(Follower follower) {
        Path1 = follower.pathBuilder()
        .addPath(
                new BezierLine(
                        new Pose(119.138, 135.398),
                        new Pose(110.000, 106.889)
                )
        )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))
                .build();
        Path2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(110.000, 106.889),
                                new Pose(99.727, 82.816)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))
                .build();
        Path3 = follower.pathBuilder()
                 .addPath(
                        new BezierLine(
                                new Pose(99.727, 82.816),
                                new Pose(131.115, 83.044)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
        Path4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(131.115, 83.044),
                                new Pose(110.955, 107.272)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))
                .build();
        Path5 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(110.955, 107.272),
                                new Pose(104.021, 58.927)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))
                .build();
        Path6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(104.021, 58.927),
                                new Pose(134.885, 58.823)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();
        Path7 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(134.885, 58.823),
                                new Pose(125.204, 58.619)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();
        Path8 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(125.204, 58.619),
                                new Pose(110.147, 107.464)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))
                .build();
    }
}
