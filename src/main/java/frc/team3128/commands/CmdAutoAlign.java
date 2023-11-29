package frc.team3128.commands;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team3128.subsystems.LimelightSubsystem;
import frc.team3128.subsystems.Swerve;

public class CmdAutoAlign extends CommandBase {
    private LimelightSubsystem m_limelight;
    private Swerve m_swerve;

    public CmdAutoAlign() {
        m_limelight = LimelightSubsystem.getInstance();
        m_swerve = Swerve.getInstance();

        //get states done 

    }

    
}
