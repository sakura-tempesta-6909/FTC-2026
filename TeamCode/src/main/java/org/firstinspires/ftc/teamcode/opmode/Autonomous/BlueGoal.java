package org.firstinspires.ftc.teamcode.opmode.Autonomous;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.field.FieldManager;
import com.bylazar.field.PanelsField;
import com.bylazar.field.Style;
import com.bylazar.telemetry.PanelsTelemetry;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.math.Vector;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.PoseHistory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.delays.Delay;
import dev.nextftc.core.commands.groups.ParallelGroup;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.FollowPath;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import org.firstinspires.ftc.teamcode.command.IntakeCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.config.Const;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.path.TestPath;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;
import dev.nextftc.ftc.NextFTCOpMode;
import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Autonomous(name = "NextFTC Autonomous Program Java")
@Configurable
public class BlueGoal extends NextFTCOpMode {
    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;
    private Limelight3A limelight;
    private IMU imu;
    private double distanceToTag;
    public double distance;
    private double a;
    private double b;
    private int lastSnapshotSize = 0;
    private TestPath testPath;
   

    public BlueGoal() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }



    @Override
    public void onInit() {
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(new Pose(25.94, 130.323, Math.toRadians(140)));
        testPath = new TestPath(PedroComponent.follower());
        ShooterCommand.stopAll();
        IntakeCommand.stopIntake();
        Drawing.init();
        Drawing.drawDebug(PedroComponent.follower());
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.pipelineSwitch(0);
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.UP, RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
    }


    @Override
    public void onStartButtonPressed() {
        limelight.start();
        autonomousRoutine().schedule();
    }

    public Command autonomousRoutine() {
        return new SequentialGroup(
                new FollowPath(testPath.Path1, true, 0.3),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(testPath.Path2),
                IntakeCommand.intake(),
                new FollowPath(testPath.Path3, false, 0.3),
                IntakeCommand.stopIntake(),
                new FollowPath(testPath.Path4),
                new ParallelGroup(
                        ShooterCommand.shootArtifacts(false),
                        new Delay(3)
                ),
                ShooterCommand.stopAll(),
                new FollowPath(testPath.Path5)
//                IntakeCommand.intake(),
//                new FollowPath(testPath.Path6),
//                IntakeCommand.stopIntake(),
//                new FollowPath(testPath.Path7),
//                new ParallelGroup(
//                        ShooterCommand.shootArtifacts(true),
//                        new Delay(3)
//                ),
//                ShooterCommand.stopAll()
        );
    }

    @Override
    public void onUpdate() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        limelight.updateRobotOrientation(orientation.getYaw());
        LLResult llResult = limelight.getLatestResult();
        if (llResult != null && llResult.isValid()) {
            Pose3D botpose = llResult.getBotpose_MT2();
            distanceToTag = getDistanceToTag(llResult.getTa());
            distance = getDistance(distanceToTag);
            telemetry.addData("Distance", distance);
            telemetry.addData("DistanceToTag", distanceToTag);
            telemetry.addData("Tx", llResult.getTx());
            telemetry.addData("Ty", llResult.getTy());
            telemetry.addData("Ta", llResult.getTa());
        }
        Drawing.drawDebug(PedroComponent.follower());
    }

    public double getDistanceToTag(double Ta) {
        double scale = 196.1;
        double b = Math.pow(Ta, -0.8030557);
        double distanceToTag = scale * b;
        return distanceToTag;
    }

    public double getDistance(double distanceToTag) {
        a = Math.pow(distanceToTag, 2);
        b = a - 4225;
        distance = Math.pow(b, 0.5);
        return distance;
    }


    class Drawing {
        public static final double ROBOT_RADIUS = 9; // woah
        private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

        private static final Style robotLook = new Style(
                "", "#3F51B5", 0.75
        );
        private static final Style historyLook = new Style(
                "", "#4CAF50", 0.75
        );

        /**
         * This prepares Panels Field for using Pedro Offsets
         */
        public static void init() {
            panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
        }

        /**
         * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
         * a Follower as an input, so an instance of the DashbaordDrawingHandler class is not needed.
         *
         * @param follower Pedro Follower instance.
         */
        public static void drawDebug(Follower follower) {
            if (follower.getCurrentPath() != null) {
                drawPath(follower.getCurrentPath(), robotLook);
                Pose closestPoint = follower.getPointFromPath(follower.getCurrentPath().getClosestPointTValue());
                drawRobot(new Pose(closestPoint.getX(), closestPoint.getY(), follower.getCurrentPath().getHeadingGoal(follower.getCurrentPath().getClosestPointTValue())), robotLook);
            }
            drawPoseHistory(follower.getPoseHistory(), historyLook);
            drawRobot(follower.getPose(), historyLook);

            sendPacket();
        }

        /**
         * This draws a robot at a specified Pose with a specified
         * look. The heading is represented as a line.
         *
         * @param pose  the Pose to draw the robot at
         * @param style the parameters used to draw the robot with
         */
        public static void drawRobot(Pose pose, Style style) {
            if (pose == null || Double.isNaN(pose.getX()) || Double.isNaN(pose.getY()) || Double.isNaN(pose.getHeading())) {
                return;
            }

            panelsField.setStyle(style);
            panelsField.moveCursor(pose.getX(), pose.getY());
            panelsField.circle(ROBOT_RADIUS);

            Vector v = pose.getHeadingAsUnitVector();
            v.setMagnitude(v.getMagnitude() * ROBOT_RADIUS);
            double x1 = pose.getX() + v.getXComponent() / 2, y1 = pose.getY() + v.getYComponent() / 2;
            double x2 = pose.getX() + v.getXComponent(), y2 = pose.getY() + v.getYComponent();

            panelsField.setStyle(style);
            panelsField.moveCursor(x1, y1);
            panelsField.line(x2, y2);
        }

        /**
         * This draws a robot at a specified Pose. The heading is represented as a line.
         *
         * @param pose the Pose to draw the robot at
         */
        public static void drawRobot(Pose pose) {
            drawRobot(pose, robotLook);
        }

        /**
         * This draws a Path with a specified look.
         *
         * @param path  the Path to draw
         * @param style the parameters used to draw the Path with
         */
        public static void drawPath(Path path, Style style) {
            double[][] points = path.getPanelsDrawingPoints();

            for (int i = 0; i < points[0].length; i++) {
                for (int j = 0; j < points.length; j++) {
                    if (Double.isNaN(points[j][i])) {
                        points[j][i] = 0;
                    }
                }
            }

            panelsField.setStyle(style);
            panelsField.moveCursor(points[0][0], points[0][1]);
            panelsField.line(points[1][0], points[1][1]);
        }

        /**
         * This draws all the Paths in a PathChain with a
         * specified look.
         *
         * @param pathChain the PathChain to draw
         * @param style     the parameters used to draw the PathChain with
         */
        public static void drawPath(PathChain pathChain, Style style) {
            for (int i = 0; i < pathChain.size(); i++) {
                drawPath(pathChain.getPath(i), style);
            }
        }

        /**
         * This draws the pose history of the robot.
         *
         * @param poseTracker the PoseHistory to get the pose history from
         * @param style       the parameters used to draw the pose history with
         */
        public static void drawPoseHistory(PoseHistory poseTracker, Style style) {
            panelsField.setStyle(style);

            int size = poseTracker.getXPositionsArray().length;
            for (int i = 0; i < size - 1; i++) {

                panelsField.moveCursor(poseTracker.getXPositionsArray()[i], poseTracker.getYPositionsArray()[i]);
                panelsField.line(poseTracker.getXPositionsArray()[i + 1], poseTracker.getYPositionsArray()[i + 1]);
            }
        }

        /**
         * This draws the pose history of the robot.
         *
         * @param poseTracker the PoseHistory to get the pose history from
         */
        public static void drawPoseHistory(PoseHistory poseTracker) {
            drawPoseHistory(poseTracker, historyLook);
        }

        /**
         * This tries to send the current packet to FTControl Panels.
         */
        public static void sendPacket() {
            panelsField.update();
        }
    }
}