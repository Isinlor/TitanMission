import javax.xml.bind.ValidationException;

public class Spacecraft extends Body implements Comparable<Spacecraft> {
    private Planet goal;
    private double shortestDistance = Double.MAX_VALUE;
    private Vector shortestDistancePoint;
    private Vector startingPosition;
    private Vector startingVelocity;

    Spacecraft (String name, double mass, Vector position, Vector velocity, Planet goal) {
        super(name, position, velocity, mass);
        startingPosition = position;
        startingVelocity = velocity;
        this.goal = goal;
    }

    double getShortestDistance() {
        return shortestDistance;
    }

    Vector getShortestDistancePoint() {
        return  shortestDistancePoint;
    }

    Vector getStartingPosition() {
        return  startingPosition;
    }

    Vector getStartingVelocity() {
        return  startingVelocity;
    }

    Planet getGoal() {
        return goal;
    }

    void setGoal(Planet goal) {
        this.goal = goal;
    }

    @Override
    void applyForce(Force force, double time) {
        super.applyForce(force, time);
        double distance = super.getPosition().euclideanDistance(goal.getPosition());
        if (distance < shortestDistance) {
            shortestDistance = distance;
            shortestDistancePoint = super.getPosition();
        }
    }

    public int compareTo(Spacecraft spacecraft) {
        return Double.compare(shortestDistance, spacecraft.getShortestDistance());
    }

    public Spacecraft clone() {
        return new Spacecraft(super.getName(), super.getMass(), startingPosition, startingVelocity, goal);
    }
}
