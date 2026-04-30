package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedNear1stackPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7;

    public RedNear1stackPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(112.377, 135.194),

                                new Pose(109.220, 113.721)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(109.220, 113.721),

                                new Pose(97.239, 84.259)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(97.239, 84.259),

                                new Pose(130.566, 83.353)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.566, 83.353),

                                new Pose(108.995, 109.082)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(108.995, 109.082),

                                new Pose(105.331, 74.704)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(105.331, 74.704),

                                new Pose(130.834, 72.596)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.834, 72.596),

                                new Pose(124.362, 93.064)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();
    }
}
