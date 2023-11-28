package frc.team3128.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import static edu.wpi.first.wpilibj2.command.Commands.*;
import frc.team3128.PositionConstants.Position;
import frc.team3128.commands.CmdAutoBalance;
import frc.team3128.common.narwhaldashboard.NarwhalDashboard;
import static frc.team3128.commands.CmdManager.*;

/**
 * Class to store information about autonomous routines.
 * @author Daniel Wang, Mason Lam
 */

public class AutoPrograms {

    public AutoPrograms() {

        Trajectories.initTrajectories();
        initAutoSelector();
    }

    private void initAutoSelector() {
        final String[] autoStrings = new String[] {
                                        //Blue Autos
                                            //Cable
                                            "cable_1Cone+1Cube", "cable_1Cone+1.5Cube","cable_1Cone+2Cube", "cable_1Cone+1.5Cube+Climb", "cable_pickup_Cube1", "cable_score_Cube1","cable_pickup_Cube2","cable_return_Cube2",
                                            //Mid
                                            "mid_1Cone+Climb","mid_1Cone+0.5Cube+Climb", "mid_1Cone+1Cube+Climb","mid_pickup_Cube2","mid_score_Cube2","mid_balance",
                                            //Hp
                                            "hp_1Cone+1Cube","hp_pickup_Cube4","hp_score_Cube4","hp_pickup_Cube3","hp_return_Cube3",

                                            "scuffedClimb"
                                            };
        NarwhalDashboard.addAutos(autoStrings);
    }

    public Command getAutonomousCommand() {
        String selectedAutoName = NarwhalDashboard.getSelectedAutoName();
        final Command autoCommand;

        if (selectedAutoName == null) {
            autoCommand = score(Position.HIGH_CONE, true);
        }

        else if (selectedAutoName == "scuffedClimb") {
            autoCommand = sequence(
                score(Position.HIGH_CONE, true),
                new CmdAutoBalance(false)
            );
        }

        else {
            selectedAutoName = ((DriverStation.getAlliance() == Alliance.Red) ? "r_" : "b_") + selectedAutoName;
            autoCommand = Trajectories.get(selectedAutoName);
        }

        return autoCommand.beforeStarting(Trajectories.resetAuto());
    }
}