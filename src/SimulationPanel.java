import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.util.Map;
import java.util.function.Consumer;

public class SimulationPanel extends JPanel {

    private Bodies<BodyMetaSwing> bodies;

    private double scale;
    private double thetaX;
    private double thetaY;
    private int translationX;
    private int translationY;

    private Consumer<Bodies<BodyMetaSwing>> action;

    private Timer timer;

    SimulationPanel() {

        setPreferredSize(new Dimension(
            800, 800
        ));

//        translationX = (int)getPreferredSize().getWidth() / 2;
//        translationY = (int)getPreferredSize().getHeight() / 2;

        MouseAdapter mouseAdapter = new MouseInputAdapter() {
            int pressX;
            int pressY;
            public void mousePressed(MouseEvent mouseEvent) {
                pressX = mouseEvent.getX() - (int)thetaX;
                pressY = mouseEvent.getY() - (int)thetaY;
            }
            public void mouseDragged(MouseEvent mouseEvent) {
                thetaX = mouseEvent.getX() - pressX;
                thetaY = mouseEvent.getY() - pressY;
            }
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                scale = scale * Math.pow(1.08, mouseWheelEvent.getWheelRotation());
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        addMouseWheelListener(mouseAdapter);

        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                int step = -3;
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        translationY += step;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        translationY -= step;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        translationX += step;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        translationX -= step;
                        break;
                }
            }
        };

        addKeyListener(keyAdapter);

        Action keyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                translationY =+ 1;
            }
        };

        getInputMap().put(KeyStroke.getKeyStroke("A"), "doNothing");

    }

    SimulationPanel(double scale, Bodies<BodyMetaSwing> bodies) {

        this();

        this.scale = scale;
        this.bodies = bodies;

    }

    void setBodies(Bodies<BodyMetaSwing> bodies) {
        this.bodies = bodies;
    }

    void setScale(double scale) {
        this.scale = scale;
    }

    Timer startAnimation(Consumer<Bodies<BodyMetaSwing>> action) {
        if(timer != null) timer.stop();
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

                Vector translation = new Vector(translationX, translationY);

                body.setPosition(body.getPosition().product(1 / scale));

                body.setPosition(body.getPosition().rotateAroundAxisX(translation, thetaY / 200));

                body.setPosition(body.getPosition().rotateAroundAxisY(translation, thetaX / 200));

                body.setPosition(body.getPosition().sum(translation));

            }
        );

        Color oldColor = g.getColor();
        for(Body<BodyMetaSwing> body: displayBodies.getBodies()) {

            Vector vector = body.getPosition();

            int x = (int)Math.round(vector.x);
            int y = (int)Math.round(vector.y);

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
