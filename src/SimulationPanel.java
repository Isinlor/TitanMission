import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SimulationPanel extends JPanel {

    Bodies bodies;
    double thetaX;
    double thetaY;

    public SimulationPanel(Bodies bodies) {

        this.bodies = bodies;

        setPreferredSize(new Dimension(
                500, 500
        ));

        MouseAdapter mouseRotation = new MouseAdapter() {
            public void mouseDragged(MouseEvent mouseEvent) {
                thetaX = mouseEvent.getX();
                thetaY = mouseEvent.getY();
            }
        };

        addMouseMotionListener(mouseRotation);

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

        Vectors positions = bodies.getPositions();
        
         positions = positions.rotateAroundAxisX(new Vector(), thetaY / 100);
         positions = positions.rotateAroundAxisY(new Vector(), thetaX / 100);

        for(Vector vector: positions.getVectors()) {

            g.fillOval(
                (int)Math.round(vector.x),
                (int)Math.round(vector.y),
                4, 4
            );

        }

        // Cube is here to help visualize 3D space.
        Cube cube = new Cube(new Vector(-100, -100, -100), 200);
        cube.rotateAroundAxisX(new Vector(), thetaY / 100);
        cube.rotateAroundAxisY(new Vector(), thetaX / 100);

        cube.draw(g);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
