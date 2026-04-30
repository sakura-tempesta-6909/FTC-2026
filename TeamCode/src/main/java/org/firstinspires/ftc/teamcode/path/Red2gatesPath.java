package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class Red2gatesPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12,Path13;

    public Red2gatesPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(112.377, 135.194),

                                new Pose(105.851, 112.696)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(41))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(105.851, 112.696),

                                new Pose(95.921, 59.795)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(95.921, 59.795),

                                new Pose(136.401, 59.341)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(136.401, 59.341),

                                new Pose(113.523, 59.486)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(113.523, 59.486),

                                new Pose(130.220, 69.596)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.220, 69.596),

                                new Pose(93.514, 69.899)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(93.514, 69.899),

                                new Pose(106.303, 106.413)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(41))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(106.303, 106.413),

                                new Pose(96.587, 83.982)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(0))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(96.587, 83.982),

                                new Pose(130.514, 83.835)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.514, 83.835),

                                new Pose(117.284, 69.890)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(180))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(117.284, 69.890),

                                new Pose(130.330, 69.725)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(130.330, 69.725),

                                new Pose(106.532, 106.450)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(41))

                .build();

        Path13 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(106.532, 106.450),

                                new Pose(113.624, 95.945)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(41), Math.toRadians(41))

                .build();
    }
}
