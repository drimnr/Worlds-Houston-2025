package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.pedropathing.follower.Follower;
import com.pedropathing.localization.Pose;
import com.pedropathing.util.Constants;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.FConstants;
import org.firstinspires.ftc.teamcode.hardware.pedroPathing.constants.LConstants;

@Config
public class DrivetrainPP {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;
    Follower follower;
    private PIDController pid;
    private boolean baseInitialized = false;

    public static double p = 1.2, i = 0.0, d = 0.1;
    public static double preset_heading = 0, targetHeading = 0;

    public DrivetrainPP(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
    }
    private Pose startPose = new Pose(0, 0, 0);
    private void initialize() {
        try {
            Constants.setConstants(FConstants.class, LConstants.class);
            follower = new Follower(hardwareMap);
            follower.setStartingPose(startPose);

            pid = new PIDController(p, i, d);
            baseInitialized = true;
            telemetry.addData("PPBase", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("PPBase Error", e.getMessage());
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        telemetry.update();
    }

    public void startTeleOPDrive() {
        follower.startTeleopDrive();
    }
    public void drive(double axial, double lateral, double yaw, boolean robotCentric) {
        if (!isInitialized()) return;
        follower.setTeleOpMovementVectors(axial, lateral, yaw, robotCentric);
    }
    public void move(Gamepad gamepad1) {
        if (!isInitialized()) return;

        double axial = -gamepad1.left_stick_y;
        double lateral = -gamepad1.left_stick_x;
        double yaw = -gamepad1.right_stick_x;

        drive(axial, lateral, yaw, true);
    }
    private double heading;
    public boolean robot_centric_with_mode(Gamepad gamepad1, Boolean mode) {
        if (!isInitialized()) return false;
        heading = follower.getPose().getHeading();
        boolean rightStickActive = Math.abs(gamepad1.right_stick_x) > 0.03;
        if (heading > Math.PI) heading -= 2 * Math.PI;
        if (rightStickActive) mode = false;

        pid.setPID(p, i, d);
        double headingCorrection = pid.calculate(heading, startPose.getHeading());

        double y = -gamepad1.left_stick_y;
        double x = -gamepad1.left_stick_x;
        double rx = mode ? headingCorrection : -gamepad1.right_stick_x;

        drive(y, x, rx, !mode);
        follower.update();
        telemetry.addData("Heading", heading);
        return mode;
    }
    public void reset() {
        initialize();
    }
    private boolean isInitialized() {
        if (!baseInitialized) {
            telemetry.addData("Drivetrain Error", "Not initialized");
            telemetry.update();
        }
        return baseInitialized;
    }
}
