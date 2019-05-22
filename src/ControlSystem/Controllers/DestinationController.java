package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Spacecraft;
import Simulation.Vector;
import Utilities.Logger.ConsoleLogger;
import Utilities.Logger.Logger;
import Utilities.Logger.NullLogger;
import Utilities.Units;
import Utilities.Utils;

public class DestinationController implements Controller {

    /**
     * Change to ConsoleLogger if you want to do debugging.
     */
    static Logger logger = new NullLogger();

    private double maxThrust = 0.01;

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        Body target = spacecraft.getTarget();

        return new Command(maxThrust, 0);

    }

}
