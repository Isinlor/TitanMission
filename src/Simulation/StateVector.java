package Simulation;

public class StateVector {

    private Vector position;
    private Vector velocity;
    private Vector angularDisplacement;
    private Vector angularVelocity;

    StateVector(Vector position, Vector velocity) {
        this.position = position;
        this.velocity = velocity;
    }

    StateVector(Vector position, Vector velocity, Vector angularDisplacement, Vector angularVelocity) {
        this.position = position;
        this.velocity = velocity;
        this.angularDisplacement = angularDisplacement;
        this.angularVelocity = angularVelocity;
    }

    public Vector getPosition() {
        return position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Vector getAngularDisplacement() {
        return angularDisplacement;
    }

    public Vector getAngularVelocity() {
        return angularVelocity;
    }

}
