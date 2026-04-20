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
                                new Pose(32.722, 135.431),

                                new Pose(34.951, 109.932)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(138))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(34.951, 109.932),

                                new Pose(48.161, 83.670)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(180))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(48.161, 83.670),

                                new Pose(12.583, 84.085)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(12.583, 84.085),

                                new Pose(35.115, 109.742)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(138))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.115, 109.742),

                                new Pose(46.511, 59.677)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(46.511, 59.677),

                                new Pose(7.257, 59.441)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(7.257, 59.441),

                                new Pose(19.942, 59.351)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(19.942, 59.351),

                                new Pose(35.495, 109.361)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(138))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.495, 109.361),

                                new Pose(47.747, 35.046)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(180))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(47.747, 35.046),

                                new Pose(7.822, 35.099)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(138))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(7.822, 35.099),

                                new Pose(35.547, 109.941)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(138))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(35.547, 109.941),

                                new Pose(34.589, 65.421)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(138), Math.toRadians(138))

                .build();
    }
}
