package frc.team3128.autonomous;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.pathplanner.lib.PathPlanner;
import com.pathplanner.lib.PathPlannerTrajectory;
import com.pathplanner.lib.auto.PIDConstants;
import com.pathplanner.lib.auto.SwerveAutoBuilder;

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

    private static final HashMap<String, List<PathPlannerTrajectory>> trajectories = new HashMap<String, List<PathPlannerTrajectory>>();

    private static final HashMap<String, Command> CommandEventMap = new HashMap<String, Command>();

    private static final Swerve swerve = Swerve.getInstance();

    private static SwerveAutoBuilder builder;

    public static void initTrajectories() {
        // modularized autos
        final String[] trajectoryNames = {
                                        //Blue Autos
                                            //Cable
                                            "b-cable_pickup-Cube1", "b-cable_score-Cube1","b-cable_pickup-Cube2","b-cable_return-Cube2",
                                            //Mid
                                            "b-mid_pickup-Cube2","b-mid_score-Cube2","b-mid_balance",
                                            //Hp
                                            "b-hp_pickup-Cube4","b-hp_score-Cube4","b-hp_pickup-Cube3","b-hp_return-Cube3",
                                            
                                        //Red Autos
                                        // add autos if needed
                                            //Cable
                                            "r-cable_pickup-Cube1",
                                            //Mid
                                            "r-mid_pickup-Cube2",
                                            //Hp
                                            "r-hp_pickup-Cube4", 
                                        };

        CommandEventMap.put("ScoreConeHigh", sequence(score(Position.HIGH_CONE, true)));

        CommandEventMap.put("ScoreCubeHigh", score(Position.HIGH_CUBE, true));

        CommandEventMap.put("ScoreLow", score(Position.LOW, true));
        
        CommandEventMap.put("PickupCube", pickup(Position.GROUND_CUBE, true));

        CommandEventMap.put("Neutral", sequence(retract(Position.NEUTRAL)));
        
        CommandEventMap.put("Balance", new ScheduleCommand(new CmdAutoBalance(true)));
        
        CommandEventMap.put("Balance2", new ScheduleCommand(new CmdAutoBalance(false)));

        for (final String trajectoryName : trajectoryNames) {

            if (trajectoryName.contains("mid")) {
                trajectories.put(trajectoryName, PathPlanner.loadPathGroup(trajectoryName, slow));
            } 
            else {
                trajectories.put(trajectoryName, PathPlanner.loadPathGroup(trajectoryName, fast));
            }
        }

        builder = new SwerveAutoBuilder(
            swerve::getPose,
            swerve::resetOdometry,
            swerveKinematics,
            new PIDConstants(translationKP, translationKI, translationKD),
            new PIDConstants(rotationKP, rotationKI, rotationKD),
            swerve::setModuleStates,
            CommandEventMap,
            swerve
        );
    }

    public static CommandBase generateAuto(PathPlannerTrajectory trajectory) {
        return builder.fullAuto(trajectory);
    }

    // Separates auto strings into a list of strings containing the prefix (e.g. b-hp) along with as many suffixes (e.g. pickup-Cube1)
    // Ex: "b-cable_pickup-Cube1&score-Cube1" -> {"b-cable_pickup-Cube1", "b-cable_score-Cube1"}
    public static ArrayList<String> stringToList(String name) {
        String prefix = name.split("_")[0];
        String[] autoStrings = name.split("_")[1].split("&");
        ArrayList<String> ret = new ArrayList<String>();
        for (String curAuto : autoStrings) {
            ret.add(prefix + "_" + curAuto);
        }
        return ret;
    }

    // conjoins list of strings into one complete trajectory
    public static CommandBase get(ArrayList<String> names) {
        ArrayList<PathPlannerTrajectory> curTrajectories = new ArrayList<PathPlannerTrajectory>();
        for (String name : names) {
            for (PathPlannerTrajectory curTrajectory : trajectories.get(name)) {
                curTrajectories.add(curTrajectory);
            }
        }
        return builder.fullAuto(curTrajectories);
    }

    public static CommandBase resetAuto() {
        return sequence(
            runOnce(()-> Leds.getInstance().defaultColor = Colors.AUTO),
            resetLeds(),
            resetGyro(DriverStation.getAlliance() == Alliance.Red ? 0 : 180),
            runOnce(()-> Manipulator.getInstance().set(-0.4), Manipulator.getInstance()),
            runOnce(()-> Manipulator.getInstance().isCone = true),
            resetAll(),
            retract(Position.NEUTRAL)
        );
    }
    
}