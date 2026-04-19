package org.firstinspires.ftc.teamcode.path;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class RedGoalPath {
    public PathChain Path1, Path2, Path3, Path4, Path5, Path6, Path7, Path8, Path9, Path10, Path11, Path12;

    public RedGoalPath(Follower follower) {
        Path1 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(119.138, 135.398),

                                new Pose(108.318, 110.253)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();

        Path2 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(108.318, 110.253),

                                new Pose(97.043, 82.816)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();

        Path3 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(97.043, 82.816),

                                new Pose(135.871, 83.820)
                        )
                ).setTangentHeadingInterpolation()

                .build();

        Path4 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(135.871, 83.820),

                                new Pose(108.476, 110.043)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();

        Path5 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(108.476, 110.043),

                                new Pose(96.656, 59.310)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();

        Path6 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(96.656, 59.310),

                                new Pose(138.634, 59.211)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path7 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(138.634, 59.211),

                                new Pose(125.204, 58.619)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path8 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(125.204, 58.619),

                                new Pose(102.271, 106.084)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();

        Path9 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(102.271, 106.084),

                                new Pose(95.788, 34.764)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(0))

                .build();

        Path10 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(95.788, 34.764),

                                new Pose(139.912, 35.085)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(0))

                .build();

        Path11 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(139.912, 35.085),

                                new Pose(107.996, 109.788)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(40))

                .build();

        Path12 = follower.pathBuilder().addPath(
                        new BezierLine(
                                new Pose(107.996, 109.788),

                                new Pose(107.156, 71.431)
                        )
                ).setLinearHeadingInterpolation(Math.toRadians(40), Math.toRadians(40))

                .build();
    }
}
