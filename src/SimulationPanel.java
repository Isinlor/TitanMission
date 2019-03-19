import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.function.Consumer;

public class SimulationPanel extends JPanel {

    private Bodies<BodyMetaSwing> bodies;

    private double scale;
    private double thetaX;
    private double thetaY;

    private Consumer<Bodies<BodyMetaSwing>> action;

    private Timer timer;

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

    Timer startAnimation(Consumer<Bodies<BodyMetaSwing>> action) {
        // Animate. Does repaint ~60 times a second.
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(action != null) action.accept(bodies);
                repaint();
            }
        });
        timer.start();
        return timer;
    }

    void stopAnimation() {
        timer.stop();
    }

    public void paintComponent(Graphics g) {

        // See: https://stackoverflow.com/a/13281121/893222
        // without it panel get visual artifacts after repaint
        super.paintComponent(g);

        turnAntialiasingOn(g);

        g.translate(getWidth() / 2, getHeight() / 2);

        drawBodies(g);

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

        Color oldColor = g.getColor();
        for(Body<BodyMetaSwing> body: displayBodies.getBodies()) {

            Vector vector = body.getPosition();

            int x = (int)Math.round(vector.x / scale);
            int y = (int)Math.round(vector.y / scale);

            g.setColor(Color.BLACK);

            g.fillOval(x, y, 7, 7);

            g.drawString(body.getName(), x + 15, y + 7);

            if(body.getMeta() instanceof BodyMetaSwing) {

                g.setColor(body.getMeta().getColor());

                g.fillOval(x, y, 6, 6);

            }

        }
        g.setColor(oldColor);

        // Cube is here to help visualize 3D space.
        Cube cube = new Cube(new Vector(-200, -200, -200), 400);
        cube.rotateAroundAxisX(new Vector(), thetaY / 200);
        cube.rotateAroundAxisY(new Vector(), thetaX / 200);

        cube.draw(g);

        g.drawString("Day: " + Double.toString(bodies.getTime() / (60 * 60 * 24)), getWidth() / 2 - 100, getHeight() / 2 - 20);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
