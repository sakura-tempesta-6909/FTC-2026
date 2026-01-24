package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TestPath {
    public PathChain Path1;

    public TestPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(122.304, 126.139),
                                new Pose(108.405, 117.342)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(36), Math.toRadians(36))
                .build();
    }
}
