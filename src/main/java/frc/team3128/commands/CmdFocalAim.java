package frc.team3128.commands;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import frc.team3128.subsystems.Swerve;


/*
 * To DO:
 * 1. Turn in place using gyroscope to determine the angle at which the robot will aim at the speaker
 * 2. note the angle and position
 * 3. Choose a different position and attempt to convert the location to aim in the threshold of the speaker
 * 4. use the +/- threshold and crease a function to scale the robots location to the angle at which the robot should aim
 */
public class CmdFocalAim {
    private double focalAngle; // robot relative, temporary value
    private Pose2d focalPosition; // field relative, temporary value
    private Pose2d robotPosition;
    private double angleRobot;
    private Swerve m_swerve;

    

    public CmdFocalAim(){
        m_swerve=Swerve.getInstance();
        angleRobot=m_swerve.getYaw();
        robotPosition=m_swerve.getPose();
        double coordRobotX = robotPosition.getTranslation().getX();
        double coordRobotY = robotPosition.getTranslation().getY();
        double coordFocalX = focalPosition.getTranslation().getX();
        double coordFocalY = focalPosition.getTranslation().getY();
        double angleAim = Math.atan((coordFocalY-coordRobotY)/(coordFocalX-coordRobotX));
        m_swerve.drive(new Translation2d(0, 0), angleAim, false);
        

    }
    
    
}
