package org.firstinspires.ftc.teamcode.intake;

import dev.nextftc.core.commands.Command;

public class IntakeCommand extends Command {
    private final IntakeSubsystem intake;
    private final double power;

    public IntakeCommand(IntakeSubsystem intake, double power) {
        this.intake = intake;
        this.power = power;
        requires(intake);
        setInterruptible(false);
    }

    @Override
    public boolean isDone() {
        return false; // whether or not the command is done
    }

    @Override
    public void start() {
        System.out.println("INTAKE COMMAND START");
        // executed when the command begins
    }

    @Override
    public void update() {
        // executed on every update of the command
    }

    @Override
    public void stop(boolean interrupted) {
        // executed when the command ends
    }
}

