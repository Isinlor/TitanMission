package Simulation;

import ControlSystem.Controller;
import Utilities.Metadata;

/**
 * Spacecraft is a body that can be controlled and keeps internal time.
 */
public class Spacecraft extends RotatingBody {

    private double internalTime;
    private Controller controller;

    private BufferedImage image;
    public Spacecraft(
        String name,
        Controller controller,
        Vector position,
        Vector angularDisplacement,
        Vector velocity,
        Vector angularVelocity,
        double mass,
        double radius,
        Metadata meta
    ) {
        super(name, position, angularDisplacement, velocity, angularVelocity, mass, radius, meta);
        this.controller = controller;
    }

    public void simulate(double time) {
        controller.executeCommand(this);
        super.simulate(time);
        internalTime =+ time;
    }

    double getInternalTime() {
        return internalTime;
    }

}
