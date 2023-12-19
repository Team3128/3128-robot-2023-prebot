package frc.team3128.autonomous;

import java.util.HashMap;
import java.util.List;

import com.pathplanner.lib.path.PathPlannerPath;
import com.pathplanner.lib.path.PathPlannerTrajectory;
import com.pathplanner.lib.util.HolonomicPathFollowerConfig;
import com.pathplanner.lib.util.PIDConstants;
import com.pathplanner.lib.util.ReplanningConfig;
import com.choreo.lib.Choreo;
import com.choreo.lib.ChoreoTrajectory;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;

import static edu.wpi.first.wpilibj2.command.Commands.*;
import static frc.team3128.Constants.AutoConstants.*;
import static frc.team3128.Constants.SwerveConstants.*;

import frc.team3128.Constants.LedConstants.Colors;
import frc.team3128.PositionConstants.Position;
import static frc.team3128.commands.CmdManager.*;

import frc.team3128.commands.CmdAutoBalance;
import frc.team3128.subsystems.Leds;
import frc.team3128.subsystems.Manipulator;
import frc.team3128.subsystems.Swerve;

/**
 * Store trajectories for autonomous. Edit points here. 
 * @author Daniel Wang
 */
public class Trajectories {

    private static final Swerve swerve = Swerve.getInstance();

    public static void initTrajectories() {

        NamedCommands.registerCommand("ScoreConeHigh", score(Position.HIGH_CONE, true));
        NamedCommands.registerCommand("ScoreCubeHigh", score(Position.HIGH_CUBE, true));
        NamedCommands.registerCommand("ScoreLow", score(Position.LOW, true));
        NamedCommands.registerCommand("PickupCube", pickup(Position.GROUND_CUBE, true));
        NamedCommands.registerCommand("Neutral", sequence(stopManip(), retract(Position.NEUTRAL)));
        NamedCommands.registerCommand("Balance", new ScheduleCommand(new CmdAutoBalance(true)));
        NamedCommands.registerCommand("Balance2", new ScheduleCommand(new CmdAutoBalance(false)));

        // CommandEventMap.put("ScoreConeHigh", sequence(score(Position.HIGH_CONE, true)));
        // CommandEventMap.put("ScoreCubeHigh", score(Position.HIGH_CUBE, true));
        // CommandEventMap.put("ScoreLow", score(Position.LOW, true));
        // CommandEventMap.put("PickupCube", pickup(Position.GROUND_CUBE, true));
        // CommandEventMap.put("Neutral", sequence(stopManip(), retract(Position.NEUTRAL)));
        // CommandEventMap.put("Balance", new ScheduleCommand(new CmdAutoBalance(true)));
        // CommandEventMap.put("Balance2", new ScheduleCommand(new CmdAutoBalance(false)));

        // for (final String pathName : pathNames) {
        //     PathPlanner.load
        // }

        // for (final String trajectoryName : trajectoryNames) {

        //     if (trajectoryName.contains("mid")) {
        //         trajectories.put(trajectoryName, PathPlanner.loadPathGroup(trajectoryName, slow));
        //     } 
        //     else {
        //         trajectories.put(trajectoryName, PathPlanner.loadPathGroup(trajectoryName, fast));
        //     }
        // }

        AutoBuilder.configureHolonomic(
            swerve::getPose,
            swerve::resetOdometry,
            swerve::getChasisSpeeds,
            swerve::setSwerve,
            new HolonomicPathFollowerConfig(
                new PIDConstants(translationKP, translationKI, translationKD),
                new PIDConstants(rotationKP, rotationKI, rotationKD),
                maxSpeed,
                trackWidth,
                new ReplanningConfig(false, false)
            ),
            swerve
        );
        // builder = new AutoBuilder(
        //     swerve::getPose,
        //     swerve::resetOdometry,
        //     swerveKinematics,
        //     new PIDConstants(translationKP, translationKI, translationKD),
        //     new PIDConstants(rotationKP, rotationKI, rotationKD),
        //     swerve::setModuleStates,
        //     CommandEventMap,
        //     swerve
        // );
    }

    // public static CommandBase generateAuto(PathPlannerTrajectory trajectory) {
    //     return builder.fullAuto(trajectory);
    // }

    // public static CommandBase get(String name) {
    //     return builder.fullAuto(trajectories.get(name));
    // }

    public static Command getPathPlannerAuto(String name) {
        return new PathPlannerAuto(name);
    }

    public static Command getChoreoAuto(String name) {
        return CHOREO_HASH_MAP.get(name);
    }

    public static Command getChoreoPath(String name) {
        ChoreoTrajectory traj = Choreo.getTrajectory("Test");
        return Choreo.choreoSwerveCommand(
            traj,
            swerve::getPose, 
            new PIDController(translationKP, translationKI, translationKD),
            new PIDController(translationKP, translationKI, translationKD),
            new PIDController(rotationKP, rotationKI, rotationKD),
            swerve::setSwerve,
            true,
            swerve
        );
    }

    public static CommandBase resetAuto() {
        return sequence(
            runOnce(()-> Leds.getInstance().defaultColor = Colors.AUTO),
            resetLeds(),
            resetGyro(DriverStation.getAlliance() == Alliance.Red ? 0 : 180),
            runOnce(()-> Manipulator.getInstance().set(-0.5), Manipulator.getInstance()),
            runOnce(()-> Manipulator.isCone = true),
            resetAll(),
            retract(Position.NEUTRAL)
        );
    }
    
}