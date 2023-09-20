package frc.team3128.subsystems;

import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;

import static frc.team3128.Constants.WristConstants.*;

import frc.team3128.common.hardware.motorcontroller.NAR_CANSparkMax;
import frc.team3128.common.hardware.motorcontroller.NAR_CANSparkMax.EncoderType;
import frc.team3128.common.utility.NAR_Shuffleboard;

public class Wrist extends NAR_PIDSubsystem {
    public NAR_CANSparkMax m_wrist;

    private static Wrist instance;
    
    public enum WristPosition {
        SCORE_CONE(0),
        SCORE_CUBE(0),

        NEUTRAL(0),

        SINGLE_SHELF_CONE(0),
        SINGLE_SHELF_CUBE(0),
        DOUBLE_SHELF_CONE(0),
        DOUBLE_SHELF_CUBE(0),
        GROUND_PICKUP_CONE(0),
        GROUND_PICKUP_CUBE(0);
        
        public final double wristAngle;

        private WristPosition(double wristAngle) {
            this.wristAngle = wristAngle;
        }
    }

    public static synchronized Wrist getInstance() {
        if (instance == null){
            instance = new Wrist();  
        }
        return instance;
    }

    public Wrist() {
        super(new PIDController(kP, kI, kD), kS, kV, kG);
        setkG_Function(()-> Math.cos(Units.degreesToRadians(getMeasurement())));
        setConstraints(MIN_ANGLE, MAX_ANGLE);
        configMotor();
        initShuffleboard(kS, kV, kG);
    }

    private void configMotor() {
        m_wrist = new NAR_CANSparkMax(WRIST_ID, EncoderType.Absolute, MotorType.kBrushless);
        m_wrist.setInverted(false);
        m_wrist.setIdleMode(IdleMode.kBrake);
        m_wrist.setSmartCurrentLimit(40);
        resetEncoder();
    }

    @Override
    protected void useOutput(double output, double setpoint) {
        m_wrist.set(MathUtil.clamp(output / 12.0, -1, 1));
    }

    @Override
    public double getMeasurement() {
        // for relative
        // return MathUtil.inputModulus(m_wrist.getSelectedSensorPosition() * ROTATION_TO_DEGREES / GEAR_RATIO + ANGLE_OFFSET, -180, 180);
        
        // for absolute
        return MathUtil.inputModulus(-m_wrist.getSelectedSensorPosition() * ROTATION_TO_DEGREES / GEAR_RATIO + ANGLE_OFFSET, -180, 180);
        // return MathUtil.inputModulus(-m_wrist.getSelectedSensorPosition() * 120, -180, 180);


    }

    public void resetEncoder() {
        m_wrist.setSelectedSensorPosition(0);
    }

    public void set(double power) {
        disable();
        m_wrist.set(power);
    }

    public void initShuffleboard() {
        // NAR_Shuffleboard.addData("Manipulator", "Manip current", () -> getCurrent(), 0, 1);
        // NAR_Shuffleboard.addData("Manipulator", "get", () -> m_roller.getMotorOutputPercent(), 0, 3);
        NAR_Shuffleboard.addData("Wrist", "angle", ()-> m_wrist.getSelectedSensorPosition(), 1, 1);
        NAR_Shuffleboard.addData("Wrist", "angle", ()-> m_wrist.getSelectedSensorPosition(), 1, 1);

    }

}
