package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueNear1stackPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7;

    public BlueNear1stackPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(33.148, 135.447),

                                new Pose(36.663, 114.226)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(36.663, 114.226),

                                new Pose(49.435, 84.295)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(49.435, 84.295),

                                new Pose(13.301, 83.939)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(13.301, 83.939),

                                new Pose(36.633, 114.320)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(36.633, 114.320),

                                new Pose(21.398, 69.761)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(21.398, 69.761),

                                new Pose(10.472, 69.942)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(10.472, 69.942),

                                new Pose(24.802, 90.463)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();
    }
}
