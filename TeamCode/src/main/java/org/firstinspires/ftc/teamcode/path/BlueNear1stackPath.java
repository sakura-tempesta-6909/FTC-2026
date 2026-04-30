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
                                new Pose(32.670, 135.143),

                                new Pose(30.701, 118.995)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(30.701, 118.995),

                                new Pose(46.616, 84.732)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(46.616, 84.732),

                                new Pose(13.816, 83.760)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(13.816, 83.760),

                                new Pose(33.992, 108.203)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(33.992, 108.203),

                                new Pose(34.082, 73.392)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.082, 73.392),

                                new Pose(15.029, 71.280)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.029, 71.280),

                                new Pose(25.463, 90.554)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();
    }
}
