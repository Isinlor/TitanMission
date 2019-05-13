package Visualisation;

import Simulation.Bodies;
import Simulation.Body;
import Simulation.Vector;
import Utilities.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

public class SimulationPanel extends JPanel implements SimulationCanvas {

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

    public SimulationPanel() {

        setPreferredSize(new Dimension(
            1800, 800
        ));

        translationX = getWidth() / 2;
        translationY = getHeight() / 2;

        setBackground(Color.WHITE);

    }

    public SimulationPanel(double scale, Bodies bodies) {

        this();

        setScale(scale);
        setBodies(bodies);

    }

    public void setBodies(Bodies bodies) {
        this.bodies = bodies.copy();
        this.originalBodies = bodies.copy();
        if(selectedBody == null || !bodies.hasBody(selectedBody.getName())) {
            selectedBody = bodies.getHeaviestBody();
        }
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public int getCenterX() {
        return getWidth() / 2;
    }

    public int getCenterY() {
        return getHeight() / 2;
    }

    public double getScale() {
        return scale;
    }

    public Vector transform(Vector vector) {
        return vector
            .sum(selectedBody.getPosition().product(-1))
            .product(1 / scale)
            .sum(new Vector(translationX, translationY));
    }

    public boolean isSimulating() {
        return isSimulating;
    }

    public void startSimulation(Consumer<Bodies> frameUpdate) {
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

    public void restartSimulation() {
        setBodies(this.originalBodies);
    }

    public void resumeSimulation() {
        isSimulating = true;
    }

    public void pauseSimulation() {
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

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        Color oldColor = g.getColor();
        for(Body body: displayBodies.getBodies()) {

            if(body instanceof Displayable) {
                ((Displayable) body).display(this, (Graphics2D)g);
                continue;
            }

            Vector vector = transform(body.getPosition());

            int x = (int)Math.round(vector.x) + centerX;
            int y = (int)Math.round(vector.y) + centerY;

            int scaledDiameter = (int)Math.round(body.getDiameter() / scale);
            int displaySize = Math.max(7, scaledDiameter);

            if(x + displaySize < 0 || x - displaySize > getWidth()) continue;
            if(y + displaySize < 0 || y - displaySize > getHeight()) continue;

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

//        // Cube is here to help visualize 3D space.
//        Cube cube = new Cube(new Vector(centerX - 200, centerY - 200, -200), 400);
//        cube.rotateAroundAxisX(new Vector(centerX, centerY), dragY);
//        cube.rotateAroundAxisY(new Vector(centerX, centerY), dragX);
//
//        cube.draw(g);

        g.drawString(
            "Day: " + (int)Math.floor(bodies.getTime() / (60 * 60 * 24)),
            getWidth() - 110, getHeight() - 60
        );
        g.drawString(
            "Hour: " + (int)Math.floor((bodies.getTime() % (60 * 60 * 24)) / (60 * 60)),
            getWidth() - 110, getHeight() - 40
        );
        g.drawString("Scale: " + Utils.round(scale), getWidth() - 110, getHeight() - 20);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
