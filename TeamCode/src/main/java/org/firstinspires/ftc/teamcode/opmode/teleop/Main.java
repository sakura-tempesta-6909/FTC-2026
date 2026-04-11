package org.firstinspires.ftc.teamcode.opmode.teleop;

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
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.CommandManager;
import dev.nextftc.core.commands.utility.InstantCommand;
import dev.nextftc.core.components.BindingsComponent;
import dev.nextftc.core.components.SubsystemComponent;
import dev.nextftc.extensions.pedro.PedroComponent;
import dev.nextftc.extensions.pedro.PedroDriverControlled;
import dev.nextftc.ftc.Gamepads;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.ftc.components.BulkReadComponent;
import dev.nextftc.hardware.driving.DriverControlledCommand;
import org.firstinspires.ftc.teamcode.command.ShooterCommand;
import org.firstinspires.ftc.teamcode.lib.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.routine.IntakeRoutine;
import org.firstinspires.ftc.teamcode.routine.ShootingRoutine;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

import java.util.List;

@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {

    private final PanelsTelemetry panelsTelemetry = PanelsTelemetry.INSTANCE;

    private final ElapsedTime loopTimer = new ElapsedTime();

    public Main() {
        addComponents(
                new PedroComponent(Constants::createFollower),
                new SubsystemComponent(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE),
                BulkReadComponent.INSTANCE,
                BindingsComponent.INSTANCE
        );
    }

    // TeleOp 開始時のロボット初期位置 (フィールド座標系)
    private static final Pose STARTING_POSE = new Pose(122.815, 124.882, Math.toRadians(36));

    @Override
    public void onInit() {
        telemetry = panelsTelemetry.getFtcTelemetry();
        PedroComponent.follower().setStartingPose(STARTING_POSE);
        Drawing.init();
    }

    @Override
    public void onStartButtonPressed() {
        DriverControlledCommand driverControlled = new PedroDriverControlled(
                Gamepads.gamepad2().leftStickY().negate(),
                Gamepads.gamepad2().leftStickX().negate(),
                Gamepads.gamepad2().rightStickX().negate(),
                false
        );
        Gamepads.gamepad2().rightBumper()
                        .whenTrue(() -> driverControlled.setScalar(0.2))
                        .whenFalse(() -> driverControlled.setScalar(1.0));

        driverControlled.schedule();

        // ===== ホールド系バインディング =====
        // ボタンごとに 1 つの Command をローカルで生成し、押下で schedule、
        // 離下で cancel する。lambda がローカル変数をキャプチャして保持する。

        Command shootCmd = ShootingRoutine.shootWithRetract();
        Gamepads.gamepad1().x().and(Gamepads.gamepad1().y().not())
                .whenBecomesTrue(shootCmd::schedule)
                .whenBecomesFalse(shootCmd::cancel);

        Command reverseCmd = ShooterCommand.spinUpReverse();
        Gamepads.gamepad1().y().and(Gamepads.gamepad1().x().not())
                .whenBecomesTrue(reverseCmd::schedule)
                .whenBecomesFalse(reverseCmd::cancel);

        Command intakeCmd = IntakeRoutine.intakeWithWeakFeed();
        Gamepads.gamepad1().a().and(Gamepads.gamepad1().b().not())
                .whenBecomesTrue(intakeCmd::schedule)
                .whenBecomesFalse(intakeCmd::cancel);

        Command outtakeCmd = IntakeRoutine.outtakeWithRetract();
        Gamepads.gamepad1().b().and(Gamepads.gamepad1().a().not())
                .whenBecomesTrue(outtakeCmd::schedule)
                .whenBecomesFalse(outtakeCmd::cancel);

        // Heading リセット (現在位置を維持したまま向きだけ 0 に)
        Gamepads.gamepad2().options()
                .whenBecomesTrue(new InstantCommand(() -> {
                    Pose current = PedroComponent.follower().getPose();
                    PedroComponent.follower().setPose(
                            new Pose(current.getX(), current.getY(), Math.toRadians(0)));
                }));
    }

    @Override
    public void onUpdate() {
        double dt = loopTimer.seconds();
        loopTimer.reset();

        // 現在実行中の全コマンド名を表示
        panelsTelemetry.getTelemetry().addData("dt", dt);
        panelsTelemetry.getTelemetry().addData("running",
                String.join(", ", CommandManager.INSTANCE.snapshot()));
        panelsTelemetry.getTelemetry().update();

        Drawing.drawDebug(PedroComponent.follower());
    }
}

class Drawing {
    /** ロボットの描画半径 (インチ)。 */
    public static final double ROBOT_RADIUS = 9;
    private static final FieldManager panelsField = PanelsField.INSTANCE.getField();

    private static final Style robotLook = new Style("", "#3F51B5", 0.75);
    private static final Style historyLook = new Style("", "#4CAF50", 0.75);

    /**
     * This prepares Panels Field for using Pedro Offsets
     */
    public static void init() {
        panelsField.setOffsets(PanelsField.INSTANCE.getPresets().getPEDRO_PATHING());
    }

    /**
     * This draws everything that will be used in the Follower's telemetryDebug() method. This takes
     * a Follower as an input, so an instance of the DashboardDrawingHandler class is not needed.
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
