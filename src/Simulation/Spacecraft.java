package Simulation;

import ControlSystem.*;
import Utilities.Metadata;
import Utilities.Units;
import Utilities.Utils;
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

    private double fuelMass;
    private double specificImpulse;
    private double maxThrust;

    private BufferedImage image = ImageHelper.getImageResource("spaceships/30px.png");

    private boolean info = true;

    public Spacecraft(String name, String targetName, Controller controller) {
        this(
            name,
            targetName,
            controller,
            1, 1
        );
    }

    public Spacecraft(String name, String targetName, Controller controller, double mass, double radius) {
        this(
            name,
            targetName,
            controller,
            mass, radius, 0,1
        );
    }

    public Spacecraft(String name, String targetName, Controller controller, double mass, double radius, double fuelMass, double specificImpulse) {
        this(
                name,
                targetName,
                controller,
                new Vector(), new Vector(), new Vector(), new Vector(),
                mass, radius, fuelMass, specificImpulse,
                new Metadata()
        );
    }

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
        double fuelMass,
        double specificImpulse,
        Metadata meta
    ) {

        super(name, position, angularDisplacement, velocity, angularVelocity, mass, radius, meta);
        this.controller = controller;
        this.targetName = targetName;

        this.fuelMass=fuelMass;
        setSpecificImpulse(specificImpulse);

    }

    public Command getCommand(double timeStep) {
        return controller.getCommand(this, timeStep);
    }

    @Override
    public String toString() {
        return super.toString()+" Fuel mass "+fuelMass+" Specific impulse "+specificImpulse;
    }

    public Body getTarget() {
        return bodies.getBody(targetName);
    }

    private Bodies getBodies() {
        return bodies;
    }

    public double getFuelMass() { return fuelMass; }

    public double getMass() {
        return super.getMass() + getFuelMass();
    }

    public void setFuelMass(double fuelMass) {
        this.fuelMass = fuelMass;
        if(this.fuelMass < 0) this.fuelMass = 0;
    }

    public void setSpecificImpulse(double specificImpulse) {
        if(specificImpulse == 0) throw new RuntimeException("Specific impulse must not be 0!");
        this.specificImpulse = specificImpulse;
    }

    public double getSpecificImpulse() { return specificImpulse; }

    public double getMaxThrust() {
        return maxThrust;
    }

    public void setMaxThrust(double maxThrust) {
        this.maxThrust = maxThrust;
    }

    public void setBodies(Bodies bodies) {
        if(this.bodies != null) throw new RuntimeException("A body can belong only to one set of bodies!");
        this.bodies = bodies;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }

    public void display(SimulationCanvas canvas, Graphics2D g) {

        g.setColor(Color.BLACK);

        Vector vector = canvas.transform(getPosition());

        int x = (int)Math.round(vector.x) + canvas.getCenterX();
        int y = (int)Math.round(vector.y) + canvas.getCenterY();

        int scaledDiameter = (int)Math.round(getDiameter() / canvas.getScale());
        int displaySize = Math.max(30, scaledDiameter);

        if(x + displaySize < 0 || x - displaySize > canvas.getWidth()) return;
        if(y + displaySize < 0 || y - displaySize > canvas.getHeight()) return;

        Vector velocity = getVelocity().unitVector().product(20);

        if(info) {
            g.drawLine(x, y, (int) velocity.x + x, (int) velocity.y + y);
        }

        x = x - displaySize / 2;
        y = y - displaySize / 2;

        g.drawImage(ImageHelper.rotate(image, getAngularDisplacement().z), x, y, displaySize, displaySize, null);

        g.drawString(getName(), x + (15 * 2), y + (7 * 2));

        if(info) {
            g.drawString("Altitude: " + Units.distance(getSurfaceToSurfaceDistance(getTarget())), x + (15 * 2), y + (7*2) + 20);
            g.drawString("Approach speed: " + Units.speed(getApproachSpeed(getTarget())), x + (15 * 2), y + (7*2) + 40);
        }

    }

    public Spacecraft copy() {
        Spacecraft spacecraft = new Spacecraft(
            getName(),
            targetName,
            controller,
            getPosition(),
            getAngularDisplacement(),
            getVelocity(),
            getAngularVelocity(),
            getMass(),
            getRadius(),
            fuelMass,
            specificImpulse,
            getMeta()
        );
        spacecraft.info = info;
        spacecraft.maxThrust = maxThrust;
        return spacecraft;
    }

    public Vector getExternalForces() {
        Force force = new Force();
        for (Body bodyB: bodies.getBodies()) {
            if(this == bodyB) continue; // a body does not attract itself
            if(bodyB.getMass() < 2) continue; // negligible
            force = force.sum(computeAttraction(bodyB));
        }
        return force;
    }

}
