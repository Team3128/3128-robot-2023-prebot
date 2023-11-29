package frc.team3128.subsystems;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import frc.team3128.PositionConstants.Position;

import static frc.team3128.PositionConstants.Position;

import com.revrobotics.CANSparkMax.IdleMode;

import common.core.controllers.Controller;
import common.core.controllers.Controller.Type;
import common.core.subsystems.NAR_PIDSubsystem;
import common.hardware.motorcontroller.NAR_CANSparkMax;
import common.hardware.motorcontroller.NAR_Motor.Neutral;

import static frc.team3128.Constants.ElevatorConstants.*;

public class Elevator extends NAR_PIDSubsystem {

    private NAR_CANSparkMax m_elv1, m_elv2;

    private static Elevator instance;

    public static synchronized Elevator getInstance() {
        if (instance == null){
            instance = new Elevator();  
        }
        return instance;
    }

    public Elevator() {
        super(new Controller(kP, kI, kD, kS, kV, kG, Type.POSITION, 0.02));
        setConstraints(MIN_DIST, MAX_DIST);
        configMotors();
        initShuffleboard();
        m_controller.setTolerance(ELV_TOLERANCE);
        resetEncoder();
    }

    public void startPID(Position position) {
        startPID(position.elvDist);
    }

    private void configMotors() {
        m_elv1 = new NAR_CANSparkMax(ELV1_ID);
        m_elv2 = new NAR_CANSparkMax(ELV2_ID);

        m_elv1.setInverted(false);
        m_elv2.setInverted(true);

        m_elv1.setNeutralMode(Neutral.COAST);
        m_elv2.setNeutralMode(Neutral.COAST);

        m_elv1.setCurrentLimit(CURRENT_LIMIT); 
        m_elv2.setCurrentLimit(CURRENT_LIMIT);
    }

    @Override
    protected void useOutput(double output) {
        final double power = MathUtil.clamp(output / 12.0, -1, 1);
        m_elv1.set(power);
        m_elv2.set(power);
    }

    public void set(double power) {
        disable();
        m_elv1.set(power);
        m_elv2.set(power);
    }

    public void stop() {
        set(0);
    }

    public void resetEncoder() {
        m_elv2.resetPosition(0);
    }

    @Override
    public double getMeasurement() {
        return m_elv2.getPosition() / GEAR_RATIO * SPOOL_CIRCUMFERENCE;
    }

}
