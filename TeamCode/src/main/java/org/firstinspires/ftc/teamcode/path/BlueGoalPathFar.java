package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPathFar {
    public PathChain Path1, Path2, Path3, Path4, Path5,Path6,Path7,Path8,Path9;

    public BlueGoalPathFar(Follower follower) {
        Path1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(56.202, 8.000),
                                new Pose(59.227, 16.840)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(120))
                .build();

        Path2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(59.227, 16.840),
                                new Pose(47.311, 35.882)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(47.311, 35.882),
                                new Pose(19.563, 35.790)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(19.563, 35.790),
                                new Pose(59.185, 16.664)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(120))
                .build();

        Path5 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(59.185, 16.664),
                                new Pose(8.840, 9.008)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(8.840, 9.008),
                                new Pose(59.126, 16.782)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(190), Math.toRadians(120))
                .build();

        Path7 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(59.126, 16.782),
                                new Pose(8.924, 8.950)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();

        Path8 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(8.924, 8.950),
                                new Pose(59.176, 16.966)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(190), Math.toRadians(120))
                .build();

        Path9 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(59.176, 16.966),
                                new Pose(58.681, 36.185)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
    }
}
