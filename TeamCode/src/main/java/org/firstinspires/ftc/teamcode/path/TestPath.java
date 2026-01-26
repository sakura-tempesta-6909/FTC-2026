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
                                new Pose(122.815, 126.156),

                                new Pose(110.732, 116.020)
                        )
                ).setConstantHeadingInterpolation(Math.toRadians(36))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.732, 116.020),

                                new Pose(96.097, 84.531)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(36), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(96.097, 84.531),

                                new Pose(128.239, 84.354)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(128.239, 84.354),

                                new Pose(110.451, 115.770)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(36))

                .build();
    }
}
