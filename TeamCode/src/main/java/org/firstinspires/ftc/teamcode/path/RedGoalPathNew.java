package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPathNew {
    public PathChain Path1, Path2, Path3, Path4, Path5,Path6;

    public RedGoalPathNew(Follower follower) {
        Path1 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(118.889, 122.465),
                                new Pose(95.689, 86.043)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(50))
                .build();
        Path2 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(95.689, 86.043),
                                new Pose(122.141, 81.318)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
        Path3 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(122.141, 81.318),
                                new Pose(105.228, 111.018)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
        Path4 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(105.228, 111.018),
                                new Pose(94.554, 62.755)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))
                .build();
        Path5 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(94.554, 62.755),
                                new Pose(120.613, 57.221)
                        )
                )
                .setTangentHeadingInterpolation()
                .build();
        Path6 = follower.pathBuilder()
                .addPath(
                        new BezierLine(
                                new Pose(120.613, 57.221),
                                new Pose(105.262, 111.014)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(45))
                .build();
    }
}