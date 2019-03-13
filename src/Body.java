class Body {

    private Vector position;
    private Vector velocity;
    private double mass;

    Body(Vector position, Vector velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
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

    void applyForce(Force force, double time) {
        Vector acceleration = force.computeAcceleration(mass);
        Vector changeInSpeed = acceleration.product(time);
        velocity = velocity.sum(changeInSpeed);
        position = position.sum(velocity.product(time));
    }

    Force computeAttraction(Body body) {
        double distance = computeDistance(body);
        // strength of attraction = -(G*m1*m2)/(d^2)
        double strength = -(Simulation.G * mass * body.mass) / (distance * distance);
        Vector direction = position.difference(body.position).unitVector();
        return new Force(
            direction.product(strength)
        );
    }

    private double computeDistance(Body body) {
        return position.euclideanDistance(body.position);
    }

    public String toString() {
        return "Position" + position + ", Velocity" + velocity + ", mass(" + Utils.round(mass) + ")";
    }

}
