import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class Simulation {

    /**
     * Gravitational constant; m^3*kg^−1*s^−2
     * From "CODATA recommended values of the fundamental physical constants: 2014"
     */
    static final double G = 6.67408e-11;

    static JFrame window;

    public static void main(String[] args) throws Exception {

        window = new JFrame();

        // exit after clicking close button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        Set<Body> setOfBodies = new HashSet<Body>();
        setOfBodies.add(new Body(new Vector(100, 100), new Vector(0, 0), 2e13));
        setOfBodies.add(new Body(new Vector(30, 30), new Vector(2.3, -1.5), 1));
        setOfBodies.add(new Body(new Vector(100, 150), new Vector(-5, 0), 1e10));

        Bodies bodies = new Bodies(setOfBodies);

        window.setContentPane(new SimulationPanel(bodies));

        window.pack();

        // make display visible
        window.setVisible(true);

        simulate(bodies);

    }

    public static void simulate(Bodies bodies) throws InterruptedException {

        Timer timer = new Timer(1, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.getContentPane().repaint();
            }
        });
        timer.start();

    }

}
