package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Spacecraft;

import java.util.List;

/**
 * This class allows to use many controllers as one.
 *
 * Commands of each controller will be summed together.
 */
public class CompositeController implements Controller {

    private Controller[] controllers;

    /**
     * In order to understand "..." below see:
     * https://docs.oracle.com/javase/8/docs/technotes/guides/language/varargs.html
     *
     * @param controllers An list of controllers to compose.
     */
    public CompositeController(Controller... controllers) {
        this.controllers = controllers;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {
        Command command = new NullCommand();
        for (Controller controller: controllers) {
            command = command.compose(controller.getCommand(spacecraft, timeStep));
        }
        return command;
    }

}
