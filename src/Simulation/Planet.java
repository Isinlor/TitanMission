package Simulation;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class Planet extends Body {
    private double radius;

    public Planet(String name, double mass, double radius, Vector position, Vector velocity) {
        super(name, position, velocity, mass);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

}
