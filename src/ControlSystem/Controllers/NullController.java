package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Spacecraft;

/**
 * Controller that does nothing.
 */
public class NullController implements Controller {
    public Command getCommand(Spacecraft spacecraft, double timeStep) {
        return new NullCommand();
    }
}
