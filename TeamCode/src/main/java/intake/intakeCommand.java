package intake;

import dev.nextftc.core.commands.Command;

public class intakeCommand extends Command {
    private final intakeSubsystem intake;
    private final double power;

    public intakeCommand(intakeSubsystem intake, double power) {
        this.intake = intake;
        this.power = power;
        requires(intake);
    }

    @Override
    public boolean isDone() {
        return false; // whether or not the command is done
    }

    @Override
    public void start() {
        intake.setPower(power);
        System.out.println("INTAKE COMMAND START");
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

