import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
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

        // Initialize bodies for the simulation.
        // I picked some random numbers that result in a planetary system.
        Set<Body> setOfBodies = new HashSet<Body>();
        setOfBodies.add(new Body(new Vector(0, 0), new Vector(0, 0), 2.1e13));
        setOfBodies.add(new Body(new Vector(0, 150), new Vector(-3, 0, 0.01), 1e11));
        setOfBodies.add(new Body(new Vector(6, 156), new Vector(-2.34, -0.5, 0.01), 1));
        setOfBodies.add(new Body(new Vector(7, 156), new Vector(-2.341, -0.5), 1));
        setOfBodies.add(new Body(new Vector(8, 156), new Vector(-2.34, -0.51), 1));
        setOfBodies.add(new Body(new Vector(9, 156), new Vector(-2.341, -0.55, 0.2), 1));
        setOfBodies.add(new Body(new Vector(0, 50), new Vector(-5, 0), 1e10));
        setOfBodies.add(new Body(new Vector(0, 350), new Vector(2, 0, -1), 2e11));

        Bodies bodies = new Bodies(setOfBodies);

        window.setContentPane(new SimulationPanel(bodies));

        window.pack();

        // make display visible
        window.setVisible(true);

        simulate(bodies);

    }

    /**
     * Animate. Does repaint ~60 times a second.
     */
    private static void simulate(Bodies bodies) throws InterruptedException {

        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.getContentPane().repaint();
            }
        });
        timer.start();

    }

}
