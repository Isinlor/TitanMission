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

        SolarSystem solarSystem = new SolarSystem();
        window.setContentPane(
            new SimulationPanel(
                 solarSystem
            )
        );

        window.pack();

        // make display visible
        window.setVisible(true);

        simulate(solarSystem);

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
