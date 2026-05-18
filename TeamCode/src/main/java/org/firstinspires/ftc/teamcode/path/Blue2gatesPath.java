package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Blue2gatesPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12,Path13,Path14;

    public Blue2gatesPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(32.485, 135.315),

                                new Pose(37.269, 113.628)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(37.269, 113.628),

                                new Pose(44.582, 84.528)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(44.582, 84.528),

                                new Pose(14.029, 83.880)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(14.029, 83.880),

                                new Pose(25.951, 74.668)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(25.951, 74.668),

                                new Pose(15.951, 72.663)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.951, 72.663),

                                new Pose(36.836, 106.861)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(36.836, 106.861),

                                new Pose(46.538, 60.047)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(46.538, 60.047),

                                new Pose(9.380, 59.817)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(9.380, 59.817),

                                new Pose(29.240, 62.053)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(29.240, 62.053),

                                new Pose(15.255, 69.715)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.255, 69.715),

                                new Pose(36.863, 106.992)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(36.863, 106.992),

                                new Pose(15.605, 69.635)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(180))

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.605, 69.635),

                                new Pose(48.894, 122.198)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();
    }
}
