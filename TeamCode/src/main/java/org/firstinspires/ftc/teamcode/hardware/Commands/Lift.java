package org.firstinspires.ftc.teamcode.hardware.Commands;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

@Config
public class Lift {
    private final HardwareMap hardwareMap;
    private Telemetry telemetry;
    private DcMotor lift_l, lift_r, lift_m;
    private PIDFController pidfController;
    private boolean liftInitialized = false;

    // Константы
    public static int high_chamber = 560, high_basket = 2080;
    public static double kp = 0.00, kd = 0.00, ki = 0.00, kf = 0.00;
    public static double down_power = 0.6, holdp = 0.1;
    public static int target_position = 0, current_position = 0;
    public static String mode = "PID";

    public Lift(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.hardwareMap = hardwareMap;
        initialize();
        reset();
    }

    private void initialize() {
        try {
            pidfController = new PIDFController(kp, kd, ki, kf);
            lift_l = hardwareMap.get(DcMotor.class, "lift_l");
            lift_r = hardwareMap.get(DcMotor.class, "lift_r");
            lift_m = hardwareMap.get(DcMotor.class, "lift_m");

            /*lift_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift_m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            lift_l.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift_r.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift_m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

            lift_r.setDirection(DcMotor.Direction.REVERSE);*/

            liftInitialized = true;
            telemetry.addData("Lift", "Initialized");
        } catch (IllegalArgumentException e) {
            telemetry.addData("Lift Error", e.getMessage());
        }

        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(telemetry, dashboard.getTelemetry());
        telemetry.update();
    }

    public void set_target_position(int position) {
        if (isInitialized()) target_position = position;
    }

    public void update_pid() {
        if (!isInitialized()) return;
        pidfController.setPIDF(kp, kd, ki, kf);
        current_position = Math.abs(lift_l.getCurrentPosition());

        if (target_position == 0 && current_position < 15) {
            setPower(0);
        } else {
            double power = pidfController.calculate(current_position, target_position);
            setPower(power);
        }
        tel();
    }
    public void setPower(double power) {
        if (isInitialized()) {
            lift_l.setPower(power);
            lift_r.setPower(power);
            lift_m.setPower(power);
        }
    }

    public int getCurrent_position() {
        return lift_l.getCurrentPosition();
    }

    public void reset() {
        if (isInitialized()) {
            lift_r.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift_r.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift_l.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift_l.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift_m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            lift_m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            lift_r.setDirection(DcMotor.Direction.FORWARD);
            lift_m.setDirection(DcMotor.Direction.REVERSE);
            lift_l.setDirection(DcMotor.Direction.REVERSE);
        }
    }

    public void tel() {
        telemetry.addData("Lift Position", getCurrent_position());
        telemetry.addData("Target Position", target_position);
        telemetry.update();
    }

    public void setHighChamber() {
        set_target_position(high_chamber);
    }

    public void setHighBasket() {
        set_target_position(high_basket);
    }

    public void setpowerTeleop(double power) {
        if (!isInitialized()) return;
        power = -power;

        if (power > 0 && Math.abs(lift_l.getCurrentPosition()) <= 2070) {
            setPower(power);
        } else if (power < 0 && Math.abs(lift_l.getCurrentPosition()) > 5) {
            setPower(power);
        } else if (Math.abs(lift_l.getCurrentPosition()) >= 10) {
            hold_position();
        } else {
            setPower(0);
        }
    }


    public void hold_position() {
        setPower(holdp);
    }

    private boolean isInitialized() {
        if (!liftInitialized) {
            telemetry.addData("Lift Error", "Not initialized");
            telemetry.update();
        }
        return liftInitialized;
    }
}
