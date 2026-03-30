package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPathFar {
    public PathChain Path1, Path2, Path3, Path4, Path5,Path6,Path7,Path8,Path9;

    public RedGoalPathFar(Follower follower) {
        Path1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(85.849, 9.210),
                                new Pose(86.050, 17.445)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(60))
                .build();

        Path2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(86.050, 17.445),
                                new Pose(94.908, 35.277)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(94.908, 35.277),
                                new Pose(126.857, 35.588)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(126.857, 35.588),
                                new Pose(86.521, 17.748)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(60))
                .build();

        Path5 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(86.521, 17.748),
                                new Pose(135.294, 9.008)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(135.294, 9.008),
                                new Pose(86.151, 17.588)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(350), Math.toRadians(60))
                .build();

        Path7 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(86.151, 17.588),
                                new Pose(135.176, 8.748)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path8 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(135.176, 8.748),
                                new Pose(86.202, 17.773)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(350), Math.toRadians(60))
                .build();

        Path9 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(86.202, 17.773),
                                new Pose(86.118, 34.487)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
}
