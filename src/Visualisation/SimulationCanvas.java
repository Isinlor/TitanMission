package Visualisation;

import Simulation.Vector;

public interface SimulationCanvas {
    int getWidth();
    int getHeight();
    int getCenterX();
    int getCenterY();
    double getScale();
    Vector transform(Vector vector);
}
