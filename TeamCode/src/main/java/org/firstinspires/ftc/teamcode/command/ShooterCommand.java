package org.firstinspires.ftc.teamcode.command;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.util.ElapsedTime;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.commands.groups.SequentialGroup;
import dev.nextftc.core.commands.utility.LambdaCommand;
import org.firstinspires.ftc.teamcode.subsystem.FeederSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystem.ShooterSubsystem;

public class ShooterCommand {
    private double rpm;

    /**
     * Spin up the shooter to target RPM.
     * Completion: when target velocity is reached.
     */
    public static Command spinUp() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUp");
    }

    /**
     * Spin up the shooter in reverse direction.
     * Completion: when target velocity is reached.
     */
    public static Command spinUpReverse() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::setReverseTargetRPM)
                .setIsDone(ShooterSubsystem.INSTANCE::isAtVelocity)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("spinUpReverse");
    }

    /**
     * Stop the shooter.
     * Completion: immediate.
     */
    public static Command stop() {
        return new LambdaCommand()
                .setStart(ShooterSubsystem.INSTANCE::stop)
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE)
                .named("stopShooter");
    }

    /**
     * Retract feeder for 1 second.
     * Completion: after 1 second.
     */
    public static Command retractFeeder() {
        ElapsedTime timer = new ElapsedTime();
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.RETRACT);
                    timer.reset();
                })
                .setIsDone(() -> timer.seconds() >= 0.2)
                .setInterruptible(true)
                .requires(FeederSubsystem.INSTANCE)
                .named("retractFeeder");
    }

    public static Command stopFeeder() {
        return new LambdaCommand()
                .setStart(() -> {
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                })
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(FeederSubsystem.INSTANCE)
                .named("retractFeeder");
    }

    /**
     * Full shooting sequence.
     * 1. Retract feeder for 1 second
     * 2. Spin up shooter (wait until target velocity)
     * 3. Run feeder and intake
     */
    public static Command shootArtifacts(boolean isRetract) {
        SequentialGroup sequentialGroup;
        if (isRetract) {
            sequentialGroup = new SequentialGroup(
                    // 1. Retract feeder for 1 second
                    retractFeeder(),
                    stopFeeder(),
                    // 2. Spin up shooter (wait until target velocity is reached)
                    spinUp(),

                    // 3. Run feeder and intake (continuous)
                    new LambdaCommand()
                            .setStart(() -> {
                                IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                                FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                            })
                            .setIsDone(() -> true)
                            .setInterruptible(true)
                            .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)
                            .named("feedAndIntake"));
        } else {
            sequentialGroup = new SequentialGroup(
                    spinUp(),

                    // 3. Run feeder and intake (continuous)
                    new LambdaCommand()
                            .setStart(() -> {
                                IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.INTAKE);
                                FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.FEED);
                            })
                            .setIsDone(() -> true)
                            .setInterruptible(true)
                            .requires(IntakeSubsystem.INSTANCE, FeederSubsystem.INSTANCE)

                            .named("feedAndIntake"));
        }
        return sequentialGroup.
                setInterruptible(true)
                .named("shootArtifacts");
    }

    /**
     * Reverse rotation sequence.
     */
    public static Command reverseArtifacts() {
        return spinUpReverse();
    }

    /**
     * Stop all subsystems.
     */
    public static Command stopAll() {
        return new LambdaCommand()
                .setStart(() -> {
                    ShooterSubsystem.INSTANCE.stop();
                    FeederSubsystem.INSTANCE.setState(FeederSubsystem.FeederState.STOP);
                    IntakeSubsystem.INSTANCE.setState(IntakeSubsystem.IntakeState.STOP);
                })
                .setIsDone(() -> true)
                .setInterruptible(true)
                .requires(ShooterSubsystem.INSTANCE, FeederSubsystem.INSTANCE, IntakeSubsystem.INSTANCE)
                .named("stopAll");
    }
}
