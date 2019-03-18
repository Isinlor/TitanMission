/**
 * Representation of a body with position velocity and mass.
 */
class Body {

    private String name;
    private Vector position;
    private Vector velocity;
    private double mass;

    Body(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    String getName() {
        return name;
    }

    Vector getPosition() {
        return position;
    }

    Vector getVelocity() {
        return velocity;
    }

    double getMass() {
        return mass;
    }

    /**
     * Applies the given force for the specified time.
     *
     * It updates velocity as well as position.
     */
    void applyForce(Force force, double time) {
        Vector acceleration = force.computeAcceleration(mass);
        Vector changeInSpeed = acceleration.product(time);
        velocity = velocity.sum(changeInSpeed);
        position = position.sum(velocity.product(time));
    }

    void addVelocity(Vector change) {
        velocity = velocity.sum(change);
    }

    /**
     * Computes force between this and the other body based on Newton's law of universal gravitation.
     * @link https://en.wikipedia.org/wiki/Newton's_law_of_universal_gravitation#Vector_form
     */
    Force computeAttraction(Body body) {
        double distance = computeDistance(body);
        // strength of attraction = -(G*m1*m2)/(d^2)
        double strength = -(Simulation.G * mass * body.mass) / (distance * distance);
        // we need to go from scalar to vector, therefore we compute direction
        Vector direction = position.difference(body.position).unitVector();
        return new Force(
            direction.product(strength) // combine strength with direction
        );
    }

    /**
     * Compute simple euclidean distance.
     */
    double computeDistance(Body body) {
        return position.euclideanDistance(body.position);
    }

    public String toString() {
        return "Name: " + getName() + ", Position" + position + ", Velocity" + velocity + ", mass(" + Utils.round(mass) + ")";
    }

    Body copy() {
        return new Body(
            getName(),
            getPosition(),
            getVelocity(),
            getMass()
        );
    }

}
