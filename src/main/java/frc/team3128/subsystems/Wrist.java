package frc.team3128.subsystems;

import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.util.Units;

import static frc.team3128.Constants.WristConstants.*;

import static frc.team3128.PositionConstants.Position;

import common.core.controllers.Controller;
import common.core.controllers.Controller.Type;
import common.core.subsystems.NAR_PIDSubsystem;
import common.hardware.motorcontroller.NAR_CANSparkMax;
import common.hardware.motorcontroller.NAR_CANSparkMax.EncoderType;
import common.hardware.motorcontroller.NAR_Motor.Neutral;

public class Wrist extends NAR_PIDSubsystem {
    public NAR_CANSparkMax m_wrist;

    private static Wrist instance;

    public static synchronized Wrist getInstance() {
        if (instance == null){
            instance = new Wrist();  
        }
        return instance;
    }

    public Wrist() {
        super(new Controller(kP, kI, kD, kS, kV, kG, Type.POSITION, 0.02));
        setkG_Function(()-> Math.cos(Units.degreesToRadians(getSetpoint())));
        setConstraints(MIN_ANGLE, MAX_ANGLE);
        configMotor();
        initShuffleboard();
        m_controller.setTolerance(WRIST_TOLERANCE);
    }

    public void startPID(Position position) {
        startPID(position.wristAngle);
    }

    private void configMotor() {
        m_wrist = new NAR_CANSparkMax(WRIST_ID, MotorType.kBrushless, EncoderType.Relative);
        m_wrist.setInverted(false);
        m_wrist.setNeutralMode(Neutral.COAST);
        m_wrist.setCurrentLimit(40);
        resetEncoder();
    }

    @Override
    protected void useOutput(double output) {
        m_wrist.set(MathUtil.clamp(output / 12.0, -1, 1));
    }

    @Override
    public double getMeasurement() {
        return m_wrist.getPosition() * ROTATION_TO_DEGREES / GEAR_RATIO;
    }

    public void resetEncoder() {
        m_wrist.resetPosition(90 * GEAR_RATIO / ROTATION_TO_DEGREES);
    }

    public void set(double power) {
        disable();
        m_wrist.set(power);
    }

}
