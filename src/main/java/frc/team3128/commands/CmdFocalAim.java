package frc.team3128.commands;
import static frc.team3128.Constants.FocalAimConstants.turnKP;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.team3128.subsystems.Swerve;

/*
 * To DO:
 * 1. Turn in place using gyroscope to determine the angle at which the robot will aim at the speaker
 * 2. note the angle and position
 * 3. Choose a different position and attempt to convert the location to aim in the threshold of the speaker
 * 4. use the +/- threshold and crease a function to scale the robots location to the angle at which the robot should aim
 */
public class CmdFocalAim extends CommandBase {
    private static PIDController controller;
    private Swerve m_swerve;
    public static double setpoint; //aka distance
    private static double rotation;

    public CmdFocalAim()
    { 
        Swerve m_swerve = Swerve.getInstance();
        controller = new PIDController(turnKP, 0, 0);
    }

    public void execute() 
    {
        setpoint = m_swerve.getSetpoint();
        //rotation needed calculated based off of the setpoint
        rotation = Units.degreesToRadians(controller.calculate(m_swerve.getGyroRotation2d().getDegrees(), setpoint));

        if (controller.atSetpoint()) 
        {
            rotation = 0;
        }
        
        m_swerve.drive(new Translation2d(4,0), rotation, false);
    }


    public void end(boolean interrupted) 
    {

    }

    
}
