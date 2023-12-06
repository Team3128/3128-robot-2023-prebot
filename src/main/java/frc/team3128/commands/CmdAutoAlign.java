package frc.team3128.commands;
import static frc.team3128.Constants.LimelightConstants.HORIZONTAL_OFFSET_GOAL;
import static frc.team3128.Constants.LimelightConstants.OBJ_KD;
import static frc.team3128.Constants.LimelightConstants.OBJ_KP;
import static frc.team3128.Constants.LimelightConstants.TX_THRESHOLD;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team3128.subsystems.LimelightSubsystem;
import frc.team3128.subsystems.Swerve;


public class CmdAutoAlign extends CommandBase {
    private LimelightSubsystem m_limelight;
    private Swerve m_swerve;
    private double targetCount;
    private double currentHorizontalOffset;
    private double previous_error;
    private double current_error;
    private double previousTime;
    private double currentTime;
    private double power; 
    private detectionStates targetState = detectionStates.SEARCHING;

    private enum detectionStates {
        SEARCHING, FEEDBACK, BLIND
    }

    public CmdAutoAlign() {
        m_limelight = LimelightSubsystem.getInstance();
        m_swerve = Swerve.getInstance();
        addRequirements(m_swerve);
       
    }
       @Override
       public void execute() {
        switch (targetState) {
            case SEARCHING:
            m_limelight.setElement(false);
            if (m_limelight.getObjectHasValidTarget()) {
                targetCount ++;
            }
                else 
                {
                    targetCount = 0;
                    targetState = detectionStates.BLIND;
                }

            //tx_threshhold for seeing how many interations is needed for the image to not be blurry
            //basically waits for a while until the interations match
            if (targetCount > TX_THRESHOLD) { 
                //find threshhold later
                currentHorizontalOffset = m_limelight.getObjectTX();
                previous_error = currentHorizontalOffset - HORIZONTAL_OFFSET_GOAL;
                //fpga thing??
                targetState = detectionStates.FEEDBACK;
                previousTime = Timer.getFPGATimestamp();
            }
            
            case FEEDBACK:
            if ( ! m_limelight.getObjectHasValidTarget()) {
                targetState = detectionStates.SEARCHING;
            }

            else 
            {
                currentHorizontalOffset = m_limelight.getObjectTX();
                currentHorizontalOffset = current_error;
                currentTime = Timer.getFPGATimestamp();
                
                power = power + OBJ_KP * current_error;
                power = power + OBJ_KD * (previous_error - current_error) / (previousTime - currentTime);
                
                power = MathUtil.clamp(power, -1, 1);
                
                m_swerve.drive(power, )
            }



        }
        }
        
       

       @Override
       public boolean isFinished() {
        return false;

       }


       @Override
       public void end(boolean interrupted) {

       }

    

    
}
