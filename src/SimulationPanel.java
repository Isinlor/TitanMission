import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class SimulationPanel extends JPanel {

    Bodies bodies;
    private Bodies originalBodies;

    double scale;
    double dragX;
    double dragY;
    int translationX;
    int translationY;

    Consumer<Bodies> action;

    private boolean isSimulating = false;

    Body selectedBody;

    SimulationPanel() {

        setPreferredSize(new Dimension(
            1800, 800
        ));

        translationX = getWidth() / 2;
        translationY = getHeight() / 2;

        setBackground(Color.WHITE);

    }

    SimulationPanel(double scale, Bodies bodies) {

        this();

        setScale(scale);
        setBodies(bodies);

    }

    void setBodies(Bodies bodies) {
        this.bodies = bodies.copy();
        this.originalBodies = bodies.copy();
        if(selectedBody == null || !bodies.hasBody(selectedBody.getName())) {
            selectedBody = bodies.getHeaviestBody();
        }
    }

    void setScale(double scale) {
        this.scale = scale;
    }

    boolean isSimulating() {
        return isSimulating;
    }

    void startSimulation(Consumer<Bodies> frameUpdate) {
        pauseSimulation();
        // Animate. Does repaint ~60 times a second.
        Timer timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (frameUpdate != null && isSimulating()) frameUpdate.accept(bodies);
                repaint();
            }
        });
        timer.start();
        resumeSimulation();
    }

    void restartSimulation() {
        setBodies(this.originalBodies);
    }

    void resumeSimulation() {
        isSimulating = true;
    }

    void pauseSimulation() {
        isSimulating = false;
    }

    public void paintComponent(Graphics g) {

        // See: https://stackoverflow.com/a/13281121/893222
        // without it panel get visual artifacts after repaint
        super.paintComponent(g);

        turnAntialiasingOn(g);

        drawBodies(g);

    }

    private void drawBodies(Graphics g) {

        Bodies displayBodies = bodies.copy();

        displayBodies.apply(
            (Body body) -> {

                Vector nullVector = new Vector();

                body.setPosition(body.getPosition().sum(selectedBody.getPosition().product(-1)));

                body.setPosition(body.getPosition().product(1 / scale));

                body.setPosition(body.getPosition().rotateAroundAxisX(nullVector, dragY));

                body.setPosition(body.getPosition().rotateAroundAxisY(nullVector, dragX));

                body.setPosition(body.getPosition().sum(new Vector(translationX, translationY)));

            }
        );

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Color oldColor = g.getColor();
        for(Body body: displayBodies.getBodies()) {

            Vector vector = body.getPosition();

            int x = (int)Math.round(vector.x) + centerX;
            int y = (int)Math.round(vector.y) + centerY;

            if(x < 0 || x > getWidth()) continue;
            if(y < 0 || y > getHeight()) continue;

            int displaySize = Math.min(getWidth() * 2, Math.max(7, (int)Math.round(body.getRadius() / scale)));

            x = x - displaySize / 2;
            y = y - displaySize / 2;

            g.setColor(Color.BLACK);

            g.fillOval(x, y, displaySize, displaySize);

            g.drawString(body.getName(), x + 15, y + 7);

            if(body.getMeta().has("color")) {

                g.setColor(Color.decode(body.getMeta().get("color")));

                g.fillOval(x, y, displaySize - 1, displaySize - 1);

            }

        }
        g.setColor(oldColor);

        // Cube is here to help visualize 3D space.
        Cube cube = new Cube(new Vector(centerX - 200, centerY - 200, -200), 400);
        cube.rotateAroundAxisX(new Vector(centerX, centerY), dragY);
        cube.rotateAroundAxisY(new Vector(centerX, centerY), dragX);

        cube.draw(g);

        g.drawString("Day: " + Double.toString(Math.round(bodies.getTime() / (60 * 60 * 24))), getWidth() - 110, getHeight() - 40);
        g.drawString("Scale: " + Utils.round(scale), getWidth() - 110, getHeight() - 20);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
