import javax.swing.*;
import javax.swing.Timer;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Simple n-body simulation based on finite differences and O(n^2) computation of forces between all bodies.
 */
class Simulation {

    /**
     * Gravitational constant; m^3*kg^−1*s^−2
     * From "CODATA recommended values of the fundamental physical constants: 2014"
     */
    static final double G = 6.67408e-11;

    static JFrame window;

    public static void main(String[] args) throws Exception {

        // speeds up things on ubuntu significantly
        // comment out if it does not work on windows / macos
        System.setProperty("sun.java2d.opengl", "true");

        window = new JFrame();

        // exit after clicking close button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SolarSystem solarSystem = new SolarSystem();

        SimulationPanel simulationPanel = new SimulationPanel(5e9, solarSystem);

        window.setContentPane(simulationPanel);

        window.pack();

        // make display visible
        window.setVisible(true);

        simulationPanel.startAnimation(
            (Bodies<BodyMetaSwing> bodies) -> {
                for (int i = 0; i < 24; i++) {
                    bodies.iterate(60*60);
                }
            }
        );

    }

}

