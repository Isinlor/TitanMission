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

    private double mass;
    private double radius = 1.0;

    private Metadata meta = new Metadata();

    public Body(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Body(String name, Vector position, Vector velocity, double mass, double radius) {
        this(name, position, velocity, mass);
        if(radius <= 0) throw new RuntimeException("Radius must be positive! Radius given:" + radius);
        this.radius = radius;
    }

    public Body(String name, Vector position, Vector velocity, double mass, double radius, Metadata meta) {
        this(name, position, velocity, mass, radius);
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public Vector getPosition() {
        return position;
    }

    /**
     * Returns the position of the given body as seen from this body.
     *
     * @param body The given body.
     *
     * @return The relative position.
     */
    public Vector getRelativePosition(Body body) {
        return body.getPosition().difference(getPosition());
    }

    public Vector getVelocity() {
        return velocity;
    }

    /**
     * It returns velocity of the given body as seen from this body.
     *
     * See: https://en.wikipedia.org/wiki/Relative_velocity
     *
     * @param body The given body.
     *
     * @return The velocity of the given body as seen from this body.
     */
    public Vector getRelativeVelocity(Body body) {
        return body.getVelocity().difference(getVelocity());
    }

    /**
     * This function gives speed at which two bodies are approaching each other.
     * In other words this is the rate of change of the distance between the bodies.
     *
     * The approach speed is positive when bodies are getting closer together.
     * It is negative when they are getting further apart.
     *
     * @param body The other body.
     *
     * @return The rate of change of distance between bodies.
     */
    public double getApproachSpeed(Body body) {
        return -getRelativeVelocity(body).dotProduct(
            getRelativePosition(body).unitVector()
        );
    }

    /**
     * Returns simple straight line euclidean distance between two bodies.
     *
     * @param body The other body.
     *
     * @return The distance.
     */
    public double getDistance(Body body) {
        return position.euclideanDistance(body.position);
    }

    /**
     * Returns surface to surface distance between two bodies.
     *
     * @param body The other body.
     *
     * @return The distance.
     */
    public double getSurfaceToSurfaceDistance(Body body) {
        return getDistance(body) - getRadius() - body.getRadius();
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
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

    public void rename(String name) {
        this.name = name;
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
        return computeAttraction(body, getDistance(body));
    }

    /**
     * Computes attraction between bodies at the given distance.
     *
     * @param body The other body.
     * @param distance The simple center of mass euclidean distance.
     *
     * @return The attraction at the given distance.
     */
    public Force computeAttraction(Body body, double distance) {
        // strength of attraction = -(G*m1*m2)/(d^2)
        double strength = -(Constants.G * mass * body.mass) / (distance * distance);
        // we need to go from scalar to vector, therefore we compute direction
        Vector direction = position.difference(body.position).unitVector();
        return new Force(
            direction.product(strength) // combine strength with direction
        );
    }

    public double computeOrbitalSpeed(double altitude) {
        return Math.sqrt(Constants.G * getMass() / (radius + altitude));
    }

    /**
     * Second escape velocity is the minimum speed needed for an object to escape from the gravitational influence of a massive body.
     * It is slower the further away from the body an object is, and slower for less massive bodies.
     */
    public double computeSecondEscapeVelocity(Body body) {
        return Math.sqrt(2 * Constants.G * getMass() / getDistance(body));
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
