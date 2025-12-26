package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import dev.nextftc.core.commands.Command;
import dev.nextftc.ftc.NextFTCOpMode;
import dev.nextftc.core.commands.CommandManager;

import intake.intakeCommand;
import intake.intakeSubsystem;


@TeleOp(name = "Main")
public class Main extends NextFTCOpMode {
    private intakeSubsystem intake;
    private Command myCommand;

    @Override
    public void onInit() {
        telemetry.addData("init", "done");
        telemetry.update();
        intake = new intakeSubsystem(hardwareMap);
        myCommand = new intakeCommand(intake,1.0);
    }

    @Override
    public void onWaitForStart() {
    }

    @Override
    public void onStartButtonPressed() {
    }

    @Override
    public void onUpdate() {
        telemetry.addLine("UPDATE LOOP");
        telemetry.update();
        CommandManager.INSTANCE.run();
        if (gamepad1.xWasPressed()) {
            telemetry.addLine("X PRESSED");
            telemetry.update();
            myCommand.schedule();
        }
        if (gamepad1.yWasPressed()) {
            telemetry.addLine("Y PRESSED");
            telemetry.update();
            myCommand.cancel();
        }
    }


    @Override
    public void onStop() {
        telemetry.addData("stop", "done");
        telemetry.update();
        CommandManager.INSTANCE.cancelAll();
    }
}




    /*
     * ドライバーがINITを押した後、PLAYを押す前に繰り返し実行するコード
     */


