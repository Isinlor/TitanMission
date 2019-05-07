package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import Simulation.Spacecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * This controller allow to record commands.
 */
public class RecordController implements Controller {

    private Controller controller;

    private List<Command> recording = new ArrayList<>();

    RecordController(Controller controller) {
        this.controller = controller;
    }

    private void record(Command command) {
        recording.add(command);
    }

    public List<Command> getRecording() {
        return recording;
    }

    public Command getCommand(Spacecraft spacecraft) {
        Command command = controller.getCommand(spacecraft);
        record(command);
        return command;
    }

}
