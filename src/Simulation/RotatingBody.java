package Simulation;

import Utilities.Metadata;
import Utilities.Utils;

/**
 * Extension of a body taking into account angular displacement and velocity of the body.
 */
public class RotatingBody extends Body {

    private Vector torque = new Vector();

    /**
     * A pseudo-vector where each coordinate indicates speed of rotation around axis of that coordinate.
     * Magnitude of coordinate indicates speed of rotation.
     * Sign of coordinate (positive/negative) indicates clockwise or counterclockwise direction.
     *
     * Unit: radians per second
     *
     * https://en.wikipedia.org/wiki/Angular_velocity
     */
    private Vector angularVelocity;

    /**
     * A pseudo-vector where each coordinate indicates rotation around axis of that coordinate.
     * Magnitude of coordinate indicates angle of rotation.
     * Sign of coordinate (positive/negative) indicates clockwise or counterclockwise direction.
     *
     * Unit: radians
     *
     * https://en.wikipedia.org/wiki/Angular_displacement
     */
    private Vector angularDisplacement;

    RotatingBody(
        String name,
        Vector position,
        Vector angularDisplacement,
        Vector velocity,
        Vector angularVelocity,
        double mass,
        double radius,
        Metadata meta
    ) {
        super(name, position, velocity, mass, radius, meta);
        this.angularVelocity = angularVelocity;
        this.angularDisplacement = angularDisplacement;
    }

    RotatingBody(String name, Vector position, Vector velocity, double mass, double radius, Metadata meta) {
        super(name, position, velocity, mass, radius, meta);
        this.angularVelocity = new Vector();
        this.angularDisplacement = new Vector();
    }

    public Vector getAngularVelocity() {
        return angularVelocity;
    }

    public Vector getAngularDisplacement() {
        return angularDisplacement;
    }

    public void simulate(double time) {
        applyTorque(torque, time);
        super.simulate(time);
    }

    public void setTorque(Vector netTorque) {
        torque = netTorque;
    }

    protected void addTorque(Vector torque) {
        this.torque = this.torque.sum(torque);
    }

    private void applyTorque(Vector torque, double time) {
        Vector acceleration = torque.quotient(getMomentOfInertia());
        Vector changeInVelocity = acceleration.product(time);
        angularVelocity = angularVelocity.sum(changeInVelocity);
        angularDisplacement = angularDisplacement.sum(angularVelocity.product(time)).mod(2*Math.PI);
    }

    /**
     * Moment of inertia is an equivalent of mass when considering the second law of motion (F=ma) for rotating body.
     * Think about ice dancer pirouette; bringing hands close to chest lowers moment of inertia making you spin faster.
     *
     * This computation assumes that the body is a solid uniform sphere.
     *
     * https://en.wikipedia.org/wiki/Moment_of_inertia
     * https://www.engineersedge.com/mechanics_machines/mass_moment_of_inertia_equations_13091.htm
     *
     * @return The moment of inertia.
     */
    double getMomentOfInertia() {
        return 2.0 / 5.0 * getMass() * getRadius();
    }

    public String toString() {
        return "" +
            "Name: " + getName() +
            ", Position" + getPosition() +
            ", Angular Displacement" + getAngularDisplacement() +
            ", Velocity" + getVelocity() +
            ", Angular Velocity" + getAngularVelocity() +
            ", mass(" + Utils.round(getMass()) + ")";
    }

    public RotatingBody copy() {
        return new RotatingBody(
            getName(),
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