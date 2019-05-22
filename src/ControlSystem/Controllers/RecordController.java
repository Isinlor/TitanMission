package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import Utilities.Recording;
import Simulation.Spacecraft;
import Utilities.Serializable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * This controller allow to record.
 */
public class RecordController<R extends Serializable> implements Controller {

    private Controller controller;

    private List<R> recording = new ArrayList<>();
    private BiFunction<Spacecraft, Command, R> createRecord;

    RecordController(Controller controller, BiFunction<Spacecraft, Command, R> createRecord) {
        this.controller = controller;
        this.createRecord = createRecord;
    }

    public Recording<R> getRecording() {
        return new Recording<R>(recording);
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {
        Command command = controller.getCommand(spacecraft, timeStep);
        recording.add(createRecord.apply(spacecraft, command));
        return command;
    }

    static public RecordController<Command> createCommandRecordController(Controller controller) {
        return new RecordController<Command>(controller, (Spacecraft s, Command command) -> {
            return command;
        });
    }

}
