import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

public class SimulationPanel extends JPanel {

    double scale;
    Bodies<BodyMetaSwing> bodies;
    double thetaX;
    double thetaY;

    SimulationPanel(double scale, Bodies<BodyMetaSwing> bodies) {

        this.scale = scale;
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

        g.translate(getWidth() / 2, getHeight() / 2);

        drawBodies(g);

        for (int i = 0; i < 24; i++) {
            bodies.iterate(60*60);
        }

    }

    private void drawBodies(Graphics g) {

        Bodies<BodyMetaSwing> displayBodies = bodies.copy();

        displayBodies.apply(
            (Body<BodyMetaSwing> body) -> {
                body.setPosition(body.getPosition().rotateAroundAxisX(new Vector(), thetaY / 200));
            }
        );

        displayBodies.apply(
            (Body<BodyMetaSwing> body) -> {
                body.setPosition(body.getPosition().rotateAroundAxisY(new Vector(), thetaX / 200));
            }
        );

        for(Body<BodyMetaSwing> body: displayBodies.getBodies()) {

            Vector vector = body.getPosition();

            g.setColor(Color.BLACK);

            g.fillOval(
                (int)Math.round(vector.x / scale),
                (int)Math.round(vector.y / scale),
                7, 7
            );

            if(body.getMeta() instanceof BodyMetaSwing) {

                g.setColor(body.getMeta().getColor());

                g.fillOval(
                    (int)Math.round(vector.x / scale),
                    (int)Math.round(vector.y / scale),
                    6, 6
                );

            }

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
