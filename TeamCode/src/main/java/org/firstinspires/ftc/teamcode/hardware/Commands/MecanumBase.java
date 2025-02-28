package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class MecanumBase {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;
    private DcMotor leftFrontDrive, leftBackDrive, rightFrontDrive, rightBackDrive;
    private PIDController pid;
    private boolean baseInitialized = false;

    public static double p = -1.2, i = 0.0, d = -0.1;
    public static double preset_heading = 0, targetHeading = 0;

    public MecanumBase(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
    }

    private void initialize() {
        try {
            leftFrontDrive = hardwareMap.get(DcMotor.class, "lfd");
            leftBackDrive = hardwareMap.get(DcMotor.class, "lbd");
            rightFrontDrive = hardwareMap.get(DcMotor.class, "rfd");
            rightBackDrive = hardwareMap.get(DcMotor.class, "rbd");

            leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
            leftBackDrive.setDirection(DcMotor.Direction.REVERSE);
            rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
            rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

            leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            pid = new PIDController(p, i, d);
            baseInitialized = true;
            telemetry.addData("MecanumBase", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("MecanumBase Error", e.getMessage());
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        telemetry.update();
    }

    // Оригинальные названия функций
    public void move(Gamepad gamepad1) {
        if (!isInitialized()) return;

        double axial = -gamepad1.left_stick_y;
        double lateral = gamepad1.left_stick_x;
        double yaw = gamepad1.right_stick_x;

        move1(axial, lateral, yaw);
    }

    public void move1(double axial, double lateral, double yaw) {
        if (!isInitialized()) return;

        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = axial - lateral - yaw;
        double leftBackPower = axial - lateral + yaw;
        double rightBackPower = axial + lateral - yaw;

        double max = Math.max(
                Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)),
                Math.max(Math.abs(leftBackPower), Math.abs(rightBackPower))
        );

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
    }

    public boolean field_centric(Gamepad gamepad1, double heading, boolean mode) {
        if (!isInitialized()) return false;

        boolean rightStickActive = Math.abs(gamepad1.right_stick_x) > 0.03;
        if (heading > Math.PI) heading -= 2 * Math.PI;
        if (rightStickActive) mode = false;

        pid.setPID(p, i, d);
        double headingCorrection = pid.calculate(heading, Math.toRadians(preset_heading));

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = mode ? headingCorrection : gamepad1.right_stick_x;

        move1(y, x, rx);
        telemetry.addData("Heading", heading);
        return mode;
    }

    public boolean robot_centric_with_mode(Gamepad gamepad1, double heading, Boolean mode) {
        if (!isInitialized()) return false;

        boolean rightStickActive = Math.abs(gamepad1.right_stick_x) > 0.03;
        if (heading > Math.PI) heading -= 2 * Math.PI;
        if (rightStickActive) mode = false;

        pid.setPID(p, i, d);
        double headingCorrection = pid.calculate(heading, Math.toRadians(preset_heading));

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = mode ? headingCorrection : gamepad1.right_stick_x;

        move1(y, x, rx);
        telemetry.addData("Heading", heading);
        return mode;
    }

    public void own_field_centric(Gamepad gamepad1, double heading) {
        if (!isInitialized()) return;

        double y = -gamepad1.left_stick_y;
        double x = gamepad1.left_stick_x;
        double rx = gamepad1.right_stick_x;

        double botHeading = heading;

        double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
        double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

        rotX *= 1.1;

        double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
        double leftFrontPower = (rotY + rotX + rx) / denominator;
        double leftBackPower = (rotY - rotX + rx) / denominator;
        double rightFrontPower = (rotY - rotX - rx) / denominator;
        double rightBackPower = (rotY + rotX - rx) / denominator;

        leftFrontDrive.setPower(leftFrontPower);
        rightFrontDrive.setPower(rightFrontPower);
        leftBackDrive.setPower(leftBackPower);
        rightBackDrive.setPower(rightBackPower);
        telemetry.addData("Heading", botHeading);
    }

    private boolean isInitialized() {
        if (!baseInitialized) {
            telemetry.addData("MecanumBase Error", "Not initialized");
            telemetry.update();
        }
        return baseInitialized;
    }
}
