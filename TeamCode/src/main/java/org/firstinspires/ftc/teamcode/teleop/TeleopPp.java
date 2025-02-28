package org.firstinspires.ftc.teamcode.teleop;

import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.hardware.Commands.DrivetrainPP;
import org.firstinspires.ftc.teamcode.hardware.Commands.Horizontal_Lift;
import org.firstinspires.ftc.teamcode.hardware.Commands.Intake;
import org.firstinspires.ftc.teamcode.hardware.Commands.Lift;
import org.firstinspires.ftc.teamcode.hardware.Commands.Outtake;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.LConstants;

@TeleOp
public class TeleopPp extends OpMode
{
    ElapsedTime timer = new ElapsedTime();
    ElapsedTime timerbt = new ElapsedTime();
    ElapsedTime timer1 = new ElapsedTime();
    ElapsedTime timerpd = new ElapsedTime();
    Lift lift;
    Intake intake;
    Outtake outtake;
    Horizontal_Lift horlift;
    DrivetrainPP drivetrain;
    String mode = "MANUAL";
    boolean outtaketake = false, take = false, peredtake = false, borttake = false;
    boolean modedrive = false;
    @Override
    public void init() {
        Constants.setConstants(FConstants.class, LConstants.class);
        modedrive = false;
        lift = new Lift(hardwareMap, telemetry);
        outtake = new Outtake(hardwareMap, telemetry);
        intake = new Intake(hardwareMap, telemetry);
        horlift = new Horizontal_Lift(hardwareMap, telemetry);
        drivetrain = new DrivetrainPP(hardwareMap, telemetry);



        telemetry.addData("Status", "Initialized");
        telemetry.update();
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
        drivetrain.startTeleOPDrive();
        timer.reset();
        timerbt.reset();
        timer1.reset();
        timerpd.reset();



        horlift.close();
        intake.setMidPovisheTake();
        intake.close();
        outtake.setZadTake();
    }

    @Override
    public void loop() {
        // intake
        if (gamepad1.dpad_up) {
            intake.rotate_Mid();
        }
        if (gamepad1.dpad_left) {
            intake.rotate_Left();
        }
        if (gamepad1.dpad_right) {
            intake.rotate_Right();
        }

        if (gamepad2.a || gamepad1.right_bumper) {
            take = true;
            timer1.reset();
            intake.setSampleTake();
            intake.open();
            intake.close();
        }
        if (take && timer1.milliseconds() > 200) {
            intake.setMidPovisheTake();
            take = false;
        }
        if (gamepad2.b) {
            intake.setMidTake();
            intake.open();
        }
        if (gamepad2.x) {
            timerpd.reset();
            peredtake = true;
            intake.open_Chut();
        }
        if(peredtake && timerpd.milliseconds() > 100){
            intake.rotate_Mid();
            intake.setPeredacha();
        }
        if(peredtake && timerpd.milliseconds() > 200){
            intake.close();
            peredtake = false;
        }


        if (gamepad2.right_bumper) {
            horlift.open();
            intake.setMidPovisheTake();
            intake.open();
        }
        //horizontal lift
        if (gamepad2.left_bumper) {
            outtake.setPeredTake();
            horlift.close();
        }



        //outtake
        if (gamepad2.dpad_up) {
            outtaketake = true;
            outtake.mayatUp1();
            outtake.grab();
            timer.reset();
        }
        if (outtaketake && timer.milliseconds() > 150) {
            intake.setMidTake();
            intake.open();
            outtake.mayatMid();outtake.setHB();outtaketake = false;
        }
        if (gamepad1.a || gamepad2.dpad_down) {
            outtake.release();
            outtake.mayatDown();
            outtake.setZadTake();
        }
        if (gamepad1.y || gamepad2.dpad_left) {
            timerbt.reset();
            borttake = true;
            outtake.grab();
        }
        if (borttake && timerbt.milliseconds() > 180) {
            outtake.setAutoSpec();
            borttake = false;
        }
        if(gamepad2.options) {
            outtake.setSpec();
        }
        if(gamepad2.share) {
            intake.setVision();
        }

        if (gamepad2.y || gamepad1.left_bumper) {
            outtake.release();
            intake.open();
        }
        if (gamepad2.left_stick_y < -0.3) {
            horlift.open();
        }
        if (gamepad2.left_stick_y > 0.3) {
            horlift.close();
        }
        if(gamepad2.touchpad) {
            intake.setAuto();
        }






















        //drivetrain
        if(gamepad1.b) modedrive = true;
        modedrive = drivetrain.robot_centric_with_mode(gamepad1, modedrive);
        if(gamepad1.start) drivetrain.reset();
        //lift
        if (mode == "PID") {
            if (gamepad2.right_stick_y != 0) {
                mode = "MANUAL";
            }
            if (Math.abs(gamepad2.left_trigger) > 0.3) {
                mode = "PID";
                lift.set_target_position(0);
            }
            if (Math.abs(gamepad2.right_trigger) > 0.3) {
                mode = "PID";
                lift.setHighBasket();
            }
            if (gamepad2.dpad_right) {
                mode = "PID";
                lift.setHighChamber();
            }
            lift.update_pid();
        }
        else {
            if (Math.abs(gamepad2.left_trigger) > 0.3) {
                mode = "PID";
                lift.set_target_position(0);
            }
            if (Math.abs(gamepad2.right_trigger) > 0.3) {
                mode = "PID";
                lift.setHighBasket();
            }
            if (gamepad2.dpad_right) {
                mode = "PID";
                lift.setHighChamber();
            }
            lift.setpowerTeleop(gamepad2.right_stick_y);
        }
    }


    @Override
    public void stop() {
    }


}
