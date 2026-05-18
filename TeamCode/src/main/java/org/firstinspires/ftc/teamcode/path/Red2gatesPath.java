package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Red2gatesPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12,Path13;

    public Red2gatesPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(112.141, 135.624),

                                new Pose(106.534, 111.022)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(106.534, 111.022),

                                new Pose(99.194, 83.517)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(99.194, 83.517),

                                new Pose(131.034, 83.616)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(131.034, 83.616),

                                new Pose(116.020, 79.425)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(116.020, 79.425),

                                new Pose(128.154, 74.735)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(128.154, 74.735),

                                new Pose(103.840, 102.555)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(103.840, 102.555),

                                new Pose(99.875, 59.582)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(99.875, 59.582),

                                new Pose(136.221, 59.502)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(136.221, 59.502),

                                new Pose(110.430, 63.639)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.430, 63.639),

                                new Pose(128.304, 69.894)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(128.304, 69.894),

                                new Pose(104.183, 102.711)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(104.183, 102.711),

                                new Pose(128.419, 71.795)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(128.419, 71.795),

                                new Pose(123.155, 95.069)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();
    }
}
