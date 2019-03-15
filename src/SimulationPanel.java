import javax.swing.*;
import java.awt.*;

public class SimulationPanel extends JPanel {

    Bodies bodies;

    public SimulationPanel(Bodies bodies) {

        this.bodies = bodies;

        setPreferredSize(new Dimension(
                500, 500
        ));

    }

    public void paintComponent(Graphics g) {

        // See: https://stackoverflow.com/a/13281121/893222
        // without it panel get visual artifacts after repaint
        super.paintComponent(g);

        turnAntialiasingOn(g);

        // actual simulation happens here; time step picked empirically
        bodies.iterate(.5);

        g.translate(getWidth() / 2, getHeight() / 2);

        drawBodies(g);

    }

    private void drawBodies(Graphics g) {

        for(Body body: bodies.getBodies()) {

            g.fillOval(
                (int)Math.round(body.getPosition().x),
                (int)Math.round(body.getPosition().y),
                4, 4
            );

        }

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
