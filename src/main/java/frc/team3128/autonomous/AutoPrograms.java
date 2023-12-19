package frc.team3128.autonomous;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import static edu.wpi.first.wpilibj2.command.Commands.*;
import frc.team3128.PositionConstants.Position;
import frc.team3128.commands.CmdAutoBalance;
import static frc.team3128.commands.CmdManager.*;

import java.util.Arrays;

import common.utility.narwhaldashboard.NarwhalDashboard;

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
            "Test"
        };
        final String[] pathStrings = new String[] {
            "Pickup1",
            "Score1"
        };
        NarwhalDashboard.getInstance().addAutos(autoStrings);
        NarwhalDashboard.getInstance().addInit("autoPaths", Arrays.asList((Object[]) pathStrings));
    }

    public Command getAutonomousCommand() {
        // String selectedAutoName = NarwhalDashboard.getInstance().getSelectedAuto();
        String selectedAutoName = "Test";
        final Command autoCommand = Trajectories.getChoreoAuto(selectedAutoName); // Trajectories.getPathPlanner(selectedAutoName);

        // if (selectedAutoName == null) {
        //     // autoCommand = score(Position.HIGH_CONE_AUTO, true);
        //     autoCommand = none();
        // }

        // else if (selectedAutoName.equals("scuffedClimb")) {
        //     autoCommand = sequence(
        //         score(Position.HIGH_CONE, true),
        //         new CmdAutoBalance(false)
        //     );
        // }

        // else {
        //     selectedAutoName = ((DriverStation.getAlliance() == Alliance.Red) ? "r_" : "b_") + selectedAutoName;
        //     autoCommand = Trajectories.get(selectedAutoName);
        // }

        return autoCommand.beforeStarting(Trajectories.resetAuto());
    }
}