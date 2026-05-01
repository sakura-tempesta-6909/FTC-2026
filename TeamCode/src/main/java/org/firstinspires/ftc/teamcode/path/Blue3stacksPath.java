package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Blue3stacksPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9,Path10, Path11, Path12, Path13,Path14;

    public Blue3stacksPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(33.456, 135.469),

                                new Pose(35.380, 113.554)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.380, 113.554),

                                new Pose(45.535, 83.868)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(45.535, 83.868),

                                new Pose(14.629, 84.163)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(14.629, 84.163),

                                new Pose(31.409, 106.114)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(31.409, 106.114),

                                new Pose(44.955, 60.353)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(44.955, 60.353),

                                new Pose(8.983, 59.916)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(8.983, 59.916),

                                new Pose(31.061, 59.787)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(31.061, 59.787),

                                new Pose(23.421, 69.957)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(23.421, 69.957),

                                new Pose(14.856, 69.948)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(14.856, 69.948),

                                new Pose(31.456, 105.990)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(139))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(31.456, 105.990),

                                new Pose(31.506, 57.799)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(31.506, 57.799),

                                new Pose(14.028, 32.124)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(14.028, 32.124),

                                new Pose(31.401, 105.973)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(240), Math.toRadians(139))

                .build();

        Path14 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(31.401, 105.973),

                                new Pose(29.735, 90.834)
                        )
                ).setTangentHeadingInterpolation()

                .build();
    }
}
