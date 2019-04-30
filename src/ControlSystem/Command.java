package ControlSystem;

/**
 * Represents a command to be executed.
 *
 * Assumes very simple spaceship controllable by thrust and torque.
 *
 *   _____
 *   | s |
 *   =====
 *    / \
 *     ;    <--- thrust
 *
 */
public class Command {

    /**
     * Magnitude of a force created by the main spaceship engine.
     *
     * This force will adjust spacecraft acceleration and by consequence velocity and position.
     *
     * The direction of thrust is dependent on spacecraft angle when the force is applied.
     *
     * See: https://en.wikipedia.org/wiki/Thrust
     */
    private double thrust;

    /**
     * Magnitude of a force that rotates a spaceship.
     *
     * This force will adjust spacecraft angular acceleration and by consequence angular velocity and angle.
     *
     * See: https://en.wikipedia.org/wiki/Torque
     */
    private double torque;

    public Command(double thrust, double torque) {
        this.thrust = thrust;
        this.torque = torque;
    }

    public double getThrust() {
        return thrust;
    }

    public double getTorque() {
        return torque;
    }

}