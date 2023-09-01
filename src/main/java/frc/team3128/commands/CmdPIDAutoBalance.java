package frc.team3128.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.PIDCommand;

import static frc.team3128.Constants.AutoConstants.*;

import frc.team3128.Constants;
import frc.team3128.subsystems.Swerve;

public class CmdPIDAutoBalance extends CommandBase{
    private final Swerve swerve;
    private double prevAngle;
    private boolean onRamp;

    PIDController controller = 
        new PIDController(
            Constants.AutoConstants.kP,
            Constants.AutoConstants.kI,
            Constants.AutoConstants.kD);

    public CmdPIDAutoBalance(boolean intialDirection) {
        swerve = Swerve.getInstance();
    }
    
    @Override
    public void initialize() {
        prevAngle = swerve.getPitch();
        onRamp = false;
    }

    @Override
    public void execute() {
        final double angle = swerve.getPitch();
        final double angleVelocity = (angle - prevAngle) / 0.02;
        prevAngle = angle;

        if (angle > RAMP_THRESHOLD) { 
            onRamp = true;
        }

        if (Math.abs(angle) < ANGLE_THRESHOLD && onRamp) {
            swerve.xlock();
            return;
        }

        if (onRamp = true) {
            double x = controller.calculate(angle, 0);
            swerve.drive(new Translation2d(x * DRIVE_SPEED * (angle > 0.0 ? -1.0 : 1.0), 0), 0, false);
        }

        swerve.drive(new Translation2d(DRIVE_SPEED * (angle > 0.0 ? -1.0 : 1.0), 0), 0, false);
    }

    @Override
    public void end(boolean interrupted) {
        swerve.xlock();
    }

    @Override
    public boolean isFinished() {
        return false;

    }

}
