package Simulation;

import ControlSystem.*;
import Utilities.Metadata;
import Utilities.Units;
import Visualisation.Displayable;
import Visualisation.ImageHelper;
import Visualisation.SimulationCanvas;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Spacecraft is a body that can be controlled and keeps internal time.
 */
public class Spacecraft extends RotatingBody implements BodiesAware, Displayable, Controllable {

    private Bodies bodies;

    private Controller controller;

    private String targetName;

    private BufferedImage image = ImageHelper.getImageResource("spaceships/15px.png");

    public Spacecraft(
        String name,
        String targetName,
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
        this.targetName = targetName;
    }

    public Command getCommand(double timeStep) {
        return controller.getCommand(this, timeStep);
    }

    public Body getTarget() {
        return bodies.getBody(targetName);
    }

    private Bodies getBodies() {
        return bodies;
    }

    public void setBodies(Bodies bodies) {
        if(this.bodies != null) throw new RuntimeException("A body can belong only to one set of bodies!");
        this.bodies = bodies;
    }

    public void display(SimulationCanvas canvas, Graphics2D g) {

        g.setColor(Color.BLACK);

        Vector vector = canvas.transform(getPosition());

        int x = (int)Math.round(vector.x) + canvas.getCenterX();
        int y = (int)Math.round(vector.y) + canvas.getCenterY();

        int scaledDiameter = (int)Math.round(getDiameter() / canvas.getScale());
        int displaySize = Math.max(15, scaledDiameter);

        if(x + displaySize < 0 || x - displaySize > canvas.getWidth()) return;
        if(y + displaySize < 0 || y - displaySize > canvas.getHeight()) return;

        Vector velocity = getVelocity().unitVector().product(20);
        g.drawLine(x, y, (int)velocity.x + x, (int)velocity.y + y);

        x = x - displaySize / 2;
        y = y - displaySize / 2;

        g.drawImage(ImageHelper.rotate(image, getAngularDisplacement().z), x, y, displaySize, displaySize, null);

        g.drawString(getName(), x + 15, y + 7);
        g.drawString("Altitude: " + Units.distance(getTarget().computeDistance(this) - getTarget().getRadius()), x + 15, y + 7 + 20);
        g.drawString("Approach speed: " + Units.speed(getApproachSpeed(getTarget())), x + 15, y + 7 + 40);

    }

    public Spacecraft copy() {
        return new Spacecraft(
            getName(),
            targetName,
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
