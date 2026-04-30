package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9,Path10, Path11, Path12, Path13;

    public BlueGoalPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(33.456, 135.469),

                                new Pose(35.535, 114.018)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.535, 114.018),

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

                                new Pose(34.039, 108.898)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(139))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.039, 108.898),

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

                                new Pose(15.378, 69.957)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.378, 69.957),

                                new Pose(34.157, 109.047)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(139))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.157, 109.047),

                                new Pose(16.365, 54.990)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(16.365, 54.990),

                                new Pose(12.563, 20.557)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(12.563, 20.557),

                                new Pose(34.244, 109.264)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(260), Math.toRadians(139))

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.244, 109.264),

                                new Pose(26.657, 96.592)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(139), Math.toRadians(139))

                .build();
    }
}
