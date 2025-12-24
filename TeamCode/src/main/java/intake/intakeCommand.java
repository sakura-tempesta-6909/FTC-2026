package intake;

import dev.nextftc.core.commands.Command;

public class intakeCommand extends Command {
    private intakeSubsystem intake;
    public intakeCommand() {
        this.intake = intake;
        requires(intake);
    }

    @Override
    public boolean isDone() {
        return false; // whether or not the command is done
    }

    @Override
    public void start() {
        intake.intakeIn();
        // executed when the command begins
    }

    @Override
    public void update() {
        // executed on every update of the command
    }

    @Override
    public void stop(boolean interrupted) {
        intake.stop();
        // executed when the command ends
    }
}

