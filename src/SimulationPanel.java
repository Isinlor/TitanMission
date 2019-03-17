import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimulationPanel extends JPanel {

    Bodies bodies;
    double thetaX;
    double thetaY;

    public SimulationPanel(Bodies bodies) {

        this.bodies = bodies;

        setPreferredSize(new Dimension(
                800, 800
        ));

        MouseAdapter mouseRotation = new MouseInputAdapter() {
            int pressX;
            int pressY;
            public void mousePressed(MouseEvent mouseEvent) {
                pressX = mouseEvent.getX() - (int)thetaX;
                pressY = mouseEvent.getY() - (int)thetaY;
                super.mousePressed(mouseEvent);
            }
            public void mouseDragged(MouseEvent mouseEvent) {
                thetaX = mouseEvent.getX() - pressX;
                thetaY = mouseEvent.getY() - pressY;
                super.mouseDragged(mouseEvent);
            }
        };

        addMouseListener(mouseRotation);
        addMouseMotionListener(mouseRotation);

    }

    public void paintComponent(Graphics g) {

        // See: https://stackoverflow.com/a/13281121/893222
        // without it panel get visual artifacts after repaint
        super.paintComponent(g);

        turnAntialiasingOn(g);

        // actual simulation happens here; time step picked empirically
        for (int i = 0; i < 1000; i++) {
            bodies.iterate(100);
        }

        g.translate(getWidth() / 2, getHeight() / 2);

        drawBodies(g);

    }

    private void drawBodies(Graphics g) {

        Vectors positions = bodies.getPositions();

        positions = positions.rotateAroundAxisX(new Vector(), thetaY / 200);
        positions = positions.rotateAroundAxisY(new Vector(), thetaX / 200);

        double scale = 5e9;

        for(Vector vector: positions.getVectors()) {

            g.fillOval(
                (int)Math.round(vector.x / scale),
                (int)Math.round(vector.y / scale),
                4, 4
            );

        }

        // Cube is here to help visualize 3D space.
        Cube cube = new Cube(new Vector(-200, -200, -200), 400);
        cube.rotateAroundAxisX(new Vector(), thetaY / 200);
        cube.rotateAroundAxisY(new Vector(), thetaX / 200);

        cube.draw(g);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
