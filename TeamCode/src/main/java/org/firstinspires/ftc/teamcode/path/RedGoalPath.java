package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12, Path13,Path14;

    public RedGoalPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(112.230, 135.047),

                                new Pose(106.583, 113.726)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(106.583, 113.726),

                                new Pose(99.297, 83.722)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(99.297, 83.722),

                                new Pose(131.264, 83.708)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(131.264, 83.708),

                                new Pose(110.580, 108.434)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.580, 108.434),

                                new Pose(97.838, 59.035)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(97.838, 59.035),

                                new Pose(134.964, 59.623)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(134.964, 59.623),

                                new Pose(106.697, 59.126)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(106.697, 59.126),

                                new Pose(121.216, 69.949)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(121.216, 69.949),

                                new Pose(131.511, 70.145)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(131.511, 70.145),

                                new Pose(110.745, 108.271)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(41))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.745, 108.271),

                                new Pose(127.520, 59.727)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(127.520, 59.727),

                                new Pose(129.627, 39.043)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(129.627, 39.043),

                                new Pose(110.545, 108.523)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(275), Math.toRadians(41))

                .build();

        Path14 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(110.545, 108.523),

                                new Pose(120.232, 93.432)
                        )
                ).setTangentHeadingInterpolation()

                .build();
    }
}
