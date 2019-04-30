package Simulation;

public class BallisticProbe extends Body implements Comparable<BallisticProbe> {
    private Body goal;
    private double shortestDistance = Double.MAX_VALUE;
    private Vector shortestDistancePoint;
    private Vector startingPosition;
    private Vector startingVelocity;

    public BallisticProbe(String name, double mass, Vector position, Vector velocity, Body goal) {
        super(name, position, velocity, mass);
        startingPosition = position;
        startingVelocity = velocity;
        this.goal = goal;
    }

    public double getShortestDistance() {
        return shortestDistance;
    }

    public Vector getShortestDistancePoint() {
        return  shortestDistancePoint;
    }

    public Vector getStartingPosition() {
        return  startingPosition;
    }

    public Vector getStartingVelocity() {
        return  startingVelocity;
    }

    public Body getGoal() {
        return goal;
    }

    public void setGoal(Body goal) {
        this.goal = goal;
    }

    public void simulate(double time) {
        super.simulate(time);
        double distance = super.getPosition().euclideanDistance(goal.getPosition());
        if (distance < shortestDistance) {
            shortestDistance = distance;
            shortestDistancePoint = super.getPosition();
        }
    }

    public int compareTo(BallisticProbe ballisticProbe) {
        return Double.compare(shortestDistance, ballisticProbe.getShortestDistance());
    }

    public BallisticProbe clone() {
        return new BallisticProbe(super.getName(), super.getMass(), startingPosition, startingVelocity, goal);
    }
}
