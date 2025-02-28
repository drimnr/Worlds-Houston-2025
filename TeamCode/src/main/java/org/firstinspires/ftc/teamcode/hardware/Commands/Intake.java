package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Intake {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;

    private Servo arm, wrist, rotate, claw;
    private boolean intakeInitialized = false;

    public static double open = 0.3, close = 0.63;
    public static double openchut = 0.63;
    public static double mid = 0.5, left = 0.8, right = 0.25;
    public static double arm_up = 0.8, arm_mid = 0.5, arm_down = 0.4;
    public static double wrist_down = 0.8, wrist_pered = 0.05, wrist_mid = 0.5, wrist_auto = 0.2;
    public static double auto_rotate = 0.08;
    public static double arm_vision = 0.55, wrist_vision = 0.12;

    public Intake(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
    }

    private void initialize() {
        try {
            arm = hardwareMap.get(Servo.class, "arm");
            wrist = hardwareMap.get(Servo.class, "wrist");
            rotate = hardwareMap.get(Servo.class, "rotate");
            claw = hardwareMap.get(Servo.class, "claw");
            intakeInitialized = true;
            telemetry.addData("Intake", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Intake Error", e.getMessage());
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        telemetry.update();
    }

    // Оригинальные названия функций
    public void open() {
        if (isInitialized()) claw.setPosition(open);
    }

    public void open_Chut() {
        if (isInitialized()) claw.setPosition(openchut);
    }

    public void close() {
        if (isInitialized()) claw.setPosition(close);
    }

    public void rotate_Auto(double angle) {
        if (isInitialized()) rotate.setPosition(mid - angle / 300);
    }

    public void rotate_Mid() {
        if (isInitialized()) rotate.setPosition(mid);
    }

    public void rotate_Left() {
        if (isInitialized()) rotate.setPosition(left);
    }

    public void rotate_Right() {
        if (isInitialized()) rotate.setPosition(right);
    }

    public void setMidTake() {
        if (isInitialized()) {
            arm.setPosition(arm_mid);
            wrist.setPosition(wrist_down);
            open();
        }
    }

    public void setMidPovisheTake() {
        if (isInitialized()) {
            arm.setPosition(arm_mid);
            wrist.setPosition(wrist_mid);
        }
    }

    public void setAuto() {
        if (isInitialized()) {
            arm.setPosition(arm_up);
            wrist.setPosition(wrist_auto);
        }
    }

    public void setSampleTake() {
        if (isInitialized()) {
            arm.setPosition(arm_down);
            wrist.setPosition(wrist_down);
        }
    }

    public void setPeredacha() {
        if (isInitialized()) {
            arm.setPosition(arm_up);
            wrist.setPosition(wrist_pered);
        }
    }

    public void setVision() {
        if (isInitialized()) {
            arm.setPosition(arm_vision);
            wrist.setPosition(wrist_vision);
        }
    }

    private boolean isInitialized() {
        if (!intakeInitialized) {
            telemetry.addData("Intake Error", "Not initialized");
            telemetry.update();
        }
        return intakeInitialized;
    }
}
