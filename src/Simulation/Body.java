package Simulation;

import Utilities.Metadata;
import Utilities.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation of a body with position velocity and mass.
 *
 * Each body is identified by name.
 *
 * You can use an instance of Metadata to store metadata.
 */
public class Body {

    private String name;
    private Vector position;
    private Vector velocity;

    private Vector startingPosition;
    private Vector startingVelocity;

    private double mass;
    private double radius = 1.0;

    private Metadata meta = new Metadata();

    public Body(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;

        startingPosition = position;
        startingVelocity = velocity;
    }

    public Body(String name, Vector position, Vector velocity, double mass, double radius) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;

        startingPosition = position;
        startingVelocity = velocity;
    }

    public Body(String name, Vector position, Vector velocity, double mass, double radius, Metadata meta) {
        this(name, position, velocity, mass, radius);
        this.meta = meta;
    }

    public Body(String name, Vector position, Vector velocity, double mass, Metadata meta) {
        this(name, position, velocity, mass);
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getRelativeVelocity(Body body) {
        return getVelocity().sum(body.getVelocity().product(-1));
    }

    public double getMass() {
        return mass;
    }

    public double getRadius() {
        return radius;
    }

    public double getDiameter() {
        return radius * 2;
    }

    public Metadata getMeta() {
        return meta;
    }

    public Vector getStartingPosition() {
        return  startingPosition;
    }

    public Vector getStartingVelocity() {
        return  startingVelocity;
    }

    public void reset() {
        position = startingPosition;
        velocity = startingVelocity;
    }

    /**
     * Applies the given force for the specified time.
     *
     * It updates velocity as well as position.
     */
    public void applyForce(Force force, double time) {
        Vector acceleration = force.computeAcceleration(mass);
        Vector changeInVelocity = acceleration.product(time);
        velocity = velocity.sum(changeInVelocity);
        position = position.sum(velocity.product(time));
    }

    public void rename(String name) {
        this.name = name;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public void addVelocity(Vector change) {
        velocity = velocity.sum(change);
    }

    public void addPosition(Vector change) { position = position.sum(change);}
    /**
     * Computes force between this and the other body based on Newton's law of universal gravitation.
     * @link https://en.wikipedia.org/wiki/Newton's_law_of_universal_gravitation#Vector_form
     */
    public Force computeAttraction(Body body) {
        double distance = computeDistance(body);
        // strength of attraction = -(G*m1*m2)/(d^2)
        double strength = -(Constants.G * mass * body.mass) / (distance * distance);
        // we need to go from scalar to vector, therefore we compute direction
        Vector direction = position.difference(body.position).unitVector();
        return new Force(
            direction.product(strength) // combine strength with direction
        );
    }

    /**
     * Compute simple euclidean distance.
     */
    public double computeDistance(Body body) {
        return position.euclideanDistance(body.position);
    }

    public double computeOrbitalSpeed(double altitude) {
        return Math.sqrt(Constants.G * getMass() / (radius + altitude));
    }

    /**
     * Second escape velocity is the minimum speed needed for an object to escape from the gravitational influence of a massive body.
     * It is slower the further away from the body an object is, and slower for less massive bodies.
     */
    public double computeSecondEscapeVelocity(Body body) {
        return Math.sqrt(2* Constants.G*getMass() / computeDistance(body));
    }

    public String toString() {
        return "Name: " + getName() + ", Position" + position + ", Velocity" + velocity + ", mass(" + Utils.round(mass) + ")";
    }

    public Body copy() {
        return new Body(
            getName(),
            getPosition(),
            getVelocity(),
            getMass(),
            getRadius(),
            getMeta()
        );
    }

    /**
     * Serialize body so that it can be saved.
     */
    public String serialize() {
        return "" +
            "name(" + name + ") " +
            "position(" + position.serialize() + ") " +
            "velocity(" + velocity.serialize() + ") " +
            "mass(" + mass + ") " +
            "radius(" + radius + ") " +
            "metadata(" + (meta != null ? meta.serialize() : "") + ")";
    }

    /**
     * Unserialize saved body.
     */
    public static Body unserialize(String string) {

        Pattern pattern = Pattern.compile("" +
            "name\\((?<name>.+)\\) " +
            "position\\((?<position>.+)\\) " +
            "velocity\\((?<velocity>.+)\\) " +
            "mass\\((?<mass>.+)\\) " +
            "radius\\((?<radius>.+)\\) " +
            "metadata\\((?<metadata>.*)\\)"
        );
        Matcher matcher = pattern.matcher(string.trim());
        matcher.matches();

        return new Body(
            matcher.group("name"),
            Vector.unserialize(matcher.group("position")),
            Vector.unserialize(matcher.group("velocity")),
            Double.parseDouble(matcher.group("mass")),
            Double.parseDouble(matcher.group("radius")),
            Metadata.unserialize(matcher.group("metadata"))
        );
    }

}
