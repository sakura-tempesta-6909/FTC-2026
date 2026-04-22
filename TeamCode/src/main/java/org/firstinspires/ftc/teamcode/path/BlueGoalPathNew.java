package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class BlueGoalPathNew {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9,Path10, Path11, Path12, Path13;

    public BlueGoalPathNew(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(32.288, 132.574),
                                new Pose(37.360, 109.932)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
                .build();
        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(37.360, 109.932),
                                new Pose(48.308, 82.351)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                .build();
        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(48.308, 82.351),
                                new Pose(13.462, 82.473)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(13.462, 82.473),
                                new Pose(36.867, 109.742)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(140))
                .build();
        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(36.867, 109.742),
                                new Pose(46.511, 58.359)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(140), Math.toRadians(180))
                .build();
        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(46.511, 58.359),
                                new Pose(8.593, 58.823)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                .build();
        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(8.593, 58.823),
                                new Pose(15.114, 58.922)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(0))
                .build();
        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(15.114, 58.922),
                                new Pose(13.250, 68.501)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();
        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(13.250, 68.501),
                                new Pose(37.047, 109.913)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(140))
                .build();
        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(37.047, 109.913),
                                new Pose(17.523, 59.798)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(17.523, 59.798),
                                new Pose(8.790, 55.334)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                .build();
        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(8.790, 55.334),
                                new Pose(8.072, 25.688)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(270))
                .build();
        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(8.072, 25.688),
                                new Pose(37.809, 109.047)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(270), Math.toRadians(140))
                .build();
    }
}
