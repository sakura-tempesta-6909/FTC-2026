package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TestPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7;

    public TestPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.620, 121.696),

                                new Pose(113.759, 115.861)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(36), Math.toRadians(36))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(113.759, 115.861),

                                new Pose(97.658, 92.696)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(36), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(97.658, 92.696),

                                new Pose(120.291, 92.810)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(120.291, 92.810),

                                new Pose(113.772, 115.848)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(36))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(113.772, 115.848),

                                new Pose(95.684, 67.418)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(36), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(95.684, 67.418),

                                new Pose(120.797, 67.038)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(120.797, 67.038),

                                new Pose(113.924, 115.797)
                        )
                ).setTangentHeadingInterpolation()

                .build();
    }
}
