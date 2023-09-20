package frc.team3128.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import static edu.wpi.first.wpilibj2.command.Commands.*;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ScheduleCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.WaitUntilCommand;
import frc.team3128.RobotContainer;
import frc.team3128.Constants.WristConstants;
import frc.team3128.common.hardware.input.NAR_XboxController;
import frc.team3128.common.narwhaldashboard.NarwhalDashboard;
import frc.team3128.subsystems.Led;
import frc.team3128.subsystems.Wrist;
import frc.team3128.subsystems.Wrist.WristPosition;
import frc.team3128.subsystems.Manipulator;
import frc.team3128.subsystems.Vision;

public class CmdManager {
    private static Led led = Led.getInstance();
    private static Wrist wrist = Wrist.getInstance();
    private static Manipulator manipulator = Manipulator.getInstance();
    private static NAR_XboxController controller = RobotContainer.controller;


    private CmdManager() {}

    public static CommandBase CmdWrist(double setpoint) {
        return sequence(
            runOnce(()-> wrist.startPID(setpoint), wrist),
            waitUntil(()-> wrist.atSetpoint())
        );
    }

    public static CommandBase CmdWrist(WristPosition position) {
        return sequence(
            runOnce(()-> wrist.startPID(position.wristAngle), wrist),
            waitUntil(()-> wrist.atSetpoint())
        );
    }

    public static CommandBase CmdMoveWrist(double power) {
        return new InstantCommand(() -> wrist.set(power), wrist);
    }

    public static CommandBase CmdManipIntake(Boolean cone) {
        return sequence(
            runOnce(()-> manipulator.intake(cone), manipulator),
            waitSeconds(0.4),
            waitUntil(()-> manipulator.hasObjectPresent()),
            waitSeconds(cone ? 0.75 : 0),
            runOnce(()-> manipulator.stallPower())
        );
    }

    public static CommandBase CmdManipOuttake() {
        return new InstantCommand(()-> manipulator.outtake(), manipulator);
    }

    public static CommandBase CmdStopManip() {
        return new InstantCommand(()-> manipulator.stopRoller(), manipulator);
    }

    public static CommandBase vibrateController() {
        return new ScheduleCommand(new WaitCommand(0.5).deadlineWith(new StartEndCommand(() -> controller.startVibrate(), () -> RobotContainer.controller.stopVibrate())));
    }
}