package Simulation;

import EventSystem.Event;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class BodyCrashedEvent implements Event {

    private Body crashedBody;

    public BodyCrashedEvent(Body crashedBody) {
        this.crashedBody = crashedBody;
    }

    public String getName() {
        return "body crashed";
    }

    public Body getCrashedBody() {
        return crashedBody;
    }

}
