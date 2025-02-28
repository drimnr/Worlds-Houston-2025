package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Outtake {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;

    private Servo mayatr, mayatl, outtake, outtake_arm;
    private boolean outtakeInitialized = false;

    // Константы положений
    public static double mayatr_up = 0.8, mayatr_up1 = 0.85, mayatr_down = 0.05, mayatr_mid = 0.48;
    public static double mayatl_up = 0.81, mayatl_down = 0.00, mayatl_mid = 0.45;
    public static double open = 0.0, close = 0.38;
    public static double pered_take = 0.9, zad_take = 0.53, spec = 0.35, hb = 0.3;
    public static double mayat_spec = 0.85, arm_spec = 0.31;
    public static double mayat_auto_spec = 0.7, arm_auto_spec = 0.43;
    public static double open_chut = 0.33;

    public Outtake(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
    }

    private void initialize() {
        try {
            outtake_arm = hardwareMap.get(Servo.class, "outtake_arm");
            outtake = hardwareMap.get(Servo.class, "outtake");
            mayatr = hardwareMap.get(Servo.class, "mayatr");
            mayatl = hardwareMap.get(Servo.class, "mayatl");
            outtakeInitialized = true;
            telemetry.addData("Outtake", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Outtake Error", e.getMessage());
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        telemetry.update();
    }

    // Оригинальные названия функций
    public void grab() {
        if (isInitialized()) outtake.setPosition(close);
    }

    public void release() {
        if (isInitialized()) outtake.setPosition(open);
    }

    public void mayatSpecimen() {
        if (isInitialized()) {
            mayatr.setPosition(mayat_spec);
            mayatl.setPosition(arm_spec);
        }
    }

    public void mayatUp() {
        if (isInitialized()) {
            mayatr.setPosition(mayatr_up);
            mayatl.setPosition(mayatl_up);
        }
    }

    public void setAutoSpec() {
        if (isInitialized()) {
            outtake_arm.setPosition(arm_auto_spec);
            mayatr.setPosition(mayat_auto_spec);
            grab();
        }
    }

    public void open_Chut() {
        if (isInitialized()) outtake.setPosition(open_chut);
    }

    public void mayatUp1() {
        if (isInitialized()) mayatr.setPosition(mayatr_up1);
    }

    public void mayatMid() {
        if (isInitialized()) {
            mayatr.setPosition(mayatr_mid);
            mayatl.setPosition(mayatl_mid);
        }
    }

    public void mayatDown() {
        if (isInitialized()) {
            mayatr.setPosition(mayatr_down);
            mayatl.setPosition(mayatl_down);
        }
    }

    public void setPeredTake() {
        if (isInitialized()) {
            outtake_arm.setPosition(pered_take);
            mayatUp();
            release();
        }
    }

    public void setZadTake() {
        if (isInitialized()) {
            outtake_arm.setPosition(zad_take);
            mayatDown();
            release();
        }
    }

    public void setAutoInit() {
        if (isInitialized()) {
            outtake_arm.setPosition(0.8);
            mayatr.setPosition(0.2);
        }
    }

    public void setHB() {
        if (isInitialized()) {
            outtake_arm.setPosition(hb);
            mayatMid();
        }
    }

    public void setSpec() {
        if (isInitialized()) {
            outtake_arm.setPosition(spec);
            mayatSpecimen();
        }
    }

    private boolean isInitialized() {
        if (!outtakeInitialized) {
            telemetry.addData("Outtake Error", "Not initialized");
            telemetry.update();
        }
        return outtakeInitialized;
    }
}
