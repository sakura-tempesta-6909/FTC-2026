package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12;

    public BlueGoalPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.040, 132.355),

                                new Pose(34.951, 109.932)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.951, 109.932),

                                new Pose(48.308, 82.351)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(48.308, 82.351),

                                new Pose(9.067, 82.215)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(9.067, 82.215),

                                new Pose(35.115, 109.742)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.115, 109.742),

                                new Pose(46.511, 58.359)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(46.511, 58.359),

                                new Pose(2.518, 58.823)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(2.518, 58.823),

                                new Pose(19.796, 58.619)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(19.796, 58.619),

                                new Pose(35.495, 109.361)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.495, 109.361),

                                new Pose(47.747, 35.046)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(47.747, 35.046),

                                new Pose(1.359, 34.530)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(1.359, 34.530),

                                new Pose(41.622, 104.641)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(41.622, 104.641),

                                new Pose(34.589, 65.421)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(140))

                .build();
    }
}
