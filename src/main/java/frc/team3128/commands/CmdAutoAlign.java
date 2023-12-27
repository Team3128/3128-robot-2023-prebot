package frc.team3128.commands;
import static frc.team3128.Constants.LimelightConstants.OBJ_KD;
import static frc.team3128.Constants.LimelightConstants.OBJ_KI;
import static frc.team3128.Constants.LimelightConstants.OBJ_KP;
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
        controller = new PIDController(OBJ_KP, OBJ_KI, OBJ_KD); //KI not used
        m_limelight = LimelightSubsystem.getInstance();
        m_swerve = Swerve.getInstance();
        addRequirements(m_swerve);

        //calls shuffleboard
        m_limelight.initShuffleboard();
        
       
    }
       @Override
       public void execute() {
        switch (targetState) {
            case SEARCHING:
            if (m_limelight.getValidTarget()) {
                targetCount ++;
            }
                else 
                {
                    targetCount = 0;
                    targetState = detectionStates.BLIND;
                }

            //tx_threshhold for seeing how many interations is needed for the image to not be blurry
            //essentially waits for a while until the interations match
            if (targetCount > TX_THRESHOLD) { 
                //find threshhold later
                targetState = detectionStates.FEEDBACK;
                controller.reset();
            }

                break;
            
            //switches to searching state if no object is detected
            case FEEDBACK:
            if ( ! m_limelight.getValidTarget()) {
                targetState = detectionStates.SEARCHING;
            }

            //if object is detected, calculate power needed to auto align 
            else 
            {
                m_measurement = m_limelight.getObjectTX();
                output = controller.calculate(m_measurement);
                m_swerve.drive(new Translation2d(4,0), output, false);
                }

                break;

            //basically rotate until object found, then switch to searching
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
