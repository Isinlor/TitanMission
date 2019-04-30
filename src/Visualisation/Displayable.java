package Visualisation;

import java.awt.*;

/**
 * Interface indicating that implementing object knows how to draw itself.
 */
public interface Displayable {
    void display(SimulationCanvas canvas, Graphics2D g);
}
