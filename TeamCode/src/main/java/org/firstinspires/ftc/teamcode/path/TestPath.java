package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TestPath {
    public PathChain Path1, Path2, Path3, Path4,Path5;

    public TestPath(Follower follower) {

        // Path 1
        Path1 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(25.9390051, 130.326392),
                        new Pose(42.9567016, 115.4570586)
                ))
                .setConstantHeadingInterpolation(Math.toRadians(140))
                .build();

        // Path 2
        Path2 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(42.9567016, 115.4570586),
                        new Pose(43.15340771, 83.59225115)
                ))
                .setLinearHeadingInterpolation(
                        Math.toRadians(140),
                        Math.toRadians(180)
                )
                .build();

        // Path 3
        Path3 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(43.15340771, 83.59225115),
                        new Pose(11.46188459, 84.1662378)
                ))
                .setConstantHeadingInterpolation(Math.toRadians(180))
                .build();

        // Path 4
        Path4 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(11.46188459, 84.1662378),
                        new Pose(43.23881131, 115.3944225)
                ))
                .setLinearHeadingInterpolation(
                        Math.toRadians(180),
                        Math.toRadians(140)
                )
                .build();
        // Path 5
        Path5 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(43.23881131, 115.3944225),
                        new Pose(43.30247718383311,59.683181225554115)
                        ))
                .setLinearHeadingInterpolation(
                        Math.toRadians(140),
                        Math.toRadians(180)
                )
                .build();
    }
}
