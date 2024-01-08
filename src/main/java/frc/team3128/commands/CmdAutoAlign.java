package frc.team3128.commands;
import static frc.team3128.Constants.LimelightConstants.KD;
import static frc.team3128.Constants.LimelightConstants.KI;
import static frc.team3128.Constants.LimelightConstants.KP;
import static frc.team3128.Constants.LimelightConstants.TX_THRESHOLD;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team3128.subsystems.LimelightSubsystem;
import frc.team3128.subsystems.Swerve;


public class CmdAutoAlign extends CommandBase {
    private LimelightSubsystem m_limelight;
    private Swerve m_swerve;
    private double targetCount;
    private double m_measurement;
    private PIDController controller;
    private double output;
    private detectionStates targetState = detectionStates.SEARCHING;

    private enum detectionStates {
        SEARCHING, FEEDBACK, BLIND
    }

    public CmdAutoAlign() {
        controller = new PIDController(KP, KI, KD); //KD not used unless no friction involved, KI not used cause feedforward
        m_limelight = LimelightSubsystem.getInstance();
        m_swerve = Swerve.getInstance();
        addRequirements(m_swerve);

        //calls shuffleboard
        m_limelight.initShuffleboard();
        
       
    }
       @Override
       public void execute() {
        switch (targetState) {
            //count goes up every time an object is detect
            case SEARCHING:
            if (m_limelight.getValidTarget()) {
                targetCount ++;
            }
                else 
                {
                    targetCount = 0;
                    targetState = detectionStates.BLIND;
                }

            //tx_threshhold is the amount of interations needed for the image to not be blurry
            //essentially waits until the count is larger than threshhold, indicating that image is clear and that it is a cube/cone
            if (targetCount > TX_THRESHOLD) { 
                //find exact threshhold later
                targetState = detectionStates.FEEDBACK;
                controller.reset();
            }

                break;
            
            //switches to searching state if no object is detected
            case FEEDBACK:
            if ( ! m_limelight.getValidTarget()) {
                targetState = detectionStates.SEARCHING;
            }

            //if object is detected, calculate power needed to align 
            else 
            {
                m_measurement = m_limelight.getObjectTX();
                output = controller.calculate(m_measurement);
                m_swerve.drive(new Translation2d(0,4), output, false);
                }

                break;

            //basically rotate until an object is detected, then switch to searching
            case BLIND:
            m_swerve.drive(new Translation2d(0, 0), 3, false); 

            if (m_limelight.getValidTarget()) {
                targetState = detectionStates.SEARCHING;
            }
            //resets error
            controller.reset();
                break;

        }

    }

       @Override
       public boolean isFinished() {
        return false;

       }


       @Override
       public void end(boolean interrupted) {
        m_swerve.stop();
       }

    

    
}
