package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11;

    public BlueGoalPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(20.494, 130.139),

                                new Pose(40.704, 112.829)
                        )
                ).setConstantHeadingInterpolation(Math.toRadians(140))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(40.704, 112.829),

                                new Pose(43.936, 90.505)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(155), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(43.936, 90.505),

                                new Pose(12.375, 90.818)
                        )
                ).setConstantHeadingInterpolation(Math.toRadians(180))

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(12.375, 90.818),

                                new Pose(40.798, 113.141)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(155))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(40.798, 113.141),

                                new Pose(44.085, 67.509)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(155), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(44.085, 67.509),

                                new Pose(11.935, 68.750)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(11.935, 68.750),

                                new Pose(40.688, 112.387)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(155))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(40.688, 112.387),

                                new Pose(42.468, 45.545)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(155), Math.toRadians(180))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(42.468, 45.545),

                                new Pose(11.094, 45.209)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(11.094, 45.209),

                                new Pose(40.579, 112.722)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(160))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(40.579, 112.722),

                                new Pose(40.150, 80.918)
                        )
                ).setTangentHeadingInterpolation()

                .build();
    }
}
