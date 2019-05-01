package Simulation;

import ControlSystem.Controller;
import Utilities.Metadata;
import Visualisation.Displayable;
import Visualisation.ImageHelper;
import Visualisation.SimulationCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Spacecraft is a body that can be controlled and keeps internal time.
 */
public class Spacecraft extends RotatingBody implements Displayable {

    private double internalTime;
    private Controller controller;

    private BufferedImage image = ImageHelper.getImageResource("spaceships/15px.png");

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

    public void display(SimulationCanvas canvas, Graphics2D g) {

        Vector vector = getPosition();

        int x = (int)Math.round(vector.x) + canvas.getCenterX();
        int y = (int)Math.round(vector.y) + canvas.getCenterY();

        int scaledDiameter = (int)Math.round(getDiameter() / canvas.getScale());
        int displaySize = Math.max(15, scaledDiameter);

        if(x + displaySize < 0 || x - displaySize > canvas.getWidth()) return;
        if(y + displaySize < 0 || y - displaySize > canvas.getHeight()) return;

        x = x - displaySize / 2;
        y = y - displaySize / 2;

        g.drawImage(ImageHelper.rotate(image, getAngularDisplacement().z), x, y, displaySize, displaySize, null);

        g.setColor(Color.BLACK);
        g.drawString(getName(), x + 15, y + 7);

    }

    public Spacecraft copy() {
        return new Spacecraft(
            getName(),
            controller,
            getPosition(),
            getAngularDisplacement(),
            getVelocity(),
            getAngularVelocity(),
            getMass(),
            getRadius(),
            getMeta()
        );
    }

}
