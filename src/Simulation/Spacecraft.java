package Simulation;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class Spacecraft extends Body implements Comparable<Spacecraft> {
    private Body goal;
    private double shortestDistance = Double.MAX_VALUE;
    private Vector shortestDistancePoint;
    private Vector startingPosition;
    private Vector startingVelocity;

    public Spacecraft (String name, double mass, Vector position, Vector velocity, Body goal) {
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

    @Override
    public void applyForce(Force force, double time) {
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