import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

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

        g.translate(getWidth() / 2, getHeight() / 2);

        drawBodies(g);

    }

    private void drawBodies(Graphics g) {

//        Vectors positions = bodies.getPositions();
//
//        positions = positions.rotateAroundAxisX(new Vector(), thetaY / 200);
//        positions = positions.rotateAroundAxisY(new Vector(), thetaX / 200);

        double scale = 0.8e9;

        for(Map.Entry<String, Body> entry: bodies.getEntries()) {

            Body body = entry.getValue();
            Vector vector = body.getPosition();

            String bodyName = entry.getKey();
            
            Color color;
            if(bodyName.equals("sun")) {
                color = Color.YELLOW;
            } else if(bodyName.equals("mars")) {
                color = Color.RED;
            } else if(bodyName.equals("earth")) {
                color = Color.BLUE;
            } else {
                color = Color.BLACK;
            }

            g.setColor(Color.BLACK);

            g.fillOval(
                (int)Math.round(vector.x / scale),
                (int)Math.round(vector.y / scale),
                7, 7
            );

            g.setColor(color);

            g.fillOval(
                (int)Math.round(vector.x / scale),
                (int)Math.round(vector.y / scale),
                6, 6
            );



        }

//        // Cube is here to help visualize 3D space.
//        Cube cube = new Cube(new Vector(-200, -200, -200), 400);
//        cube.rotateAroundAxisX(new Vector(), thetaY / 200);
//        cube.rotateAroundAxisY(new Vector(), thetaX / 200);
//
//        cube.draw(g);

    }

    private void turnAntialiasingOn(Graphics g) {
        ((Graphics2D)g).setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
    }

}
