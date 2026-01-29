package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedLeavePath {
    public PathChain Path1;

    public RedLeavePath(Follower follower) {
        Path1 = follower.pathBuilder()
                .addPath(new BezierLine(
                        new Pose(25.9390051, 130.326392),
                        new Pose(42.9567016, 115.4570586)
                ))
                .setConstantHeadingInterpolation(Math.toRadians(140))
                .build();

    }
}
