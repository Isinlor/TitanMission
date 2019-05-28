package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Utilities.Recording;
import Simulation.Spacecraft;

/**
 * Allows to replay commands from a recording.
 */
public class ReplayController implements Controller {

    private Controller controller = new DestinationController(1000000);
    private Recording<Command> recording;
    private int commandIndex = 0;

    public ReplayController(Recording<Command> recording) {
        this.recording = recording;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {
        if(commandIndex >= recording.getRecording().size()) {
            if(spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget()) < 30) return new NullCommand();
            return controller.getCommand(spacecraft, timeStep);
        }
        Command command = recording.getRecording().get(commandIndex);
        commandIndex++;
        return command;
    }

}
