import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class SimulationPanel extends JPanel {

    private Bodies<BodyMetaSwing> bodies;
    private Bodies<BodyMetaSwing> originalBodies;

    private double scale;
    private double dragX;
    private double dragY;
    private int translationX;
    private int translationY;

    private Consumer<Bodies<BodyMetaSwing>> action;

    private Timer timer;

    boolean isSimulating = false;
    Button animationButton = new Button("Pause simulation");

    Body selectedBody;
    JComboBox<String> bodySelector = new JComboBox<>();

    SimulationPanel() {

        setPreferredSize(new Dimension(
            1800, 800
        ));

        translationX = getWidth() / 2;
        translationY = getHeight() / 2;

        setBackground(Color.WHITE);

        MouseAdapter mouseAdapter = new MouseInputAdapter() {
            int currentX;
            int currentY;
            public void mousePressed(MouseEvent mouseEvent) {
                currentX = mouseEvent.getX();
                currentY = mouseEvent.getY();
            }
            public void mouseDragged(MouseEvent mouseEvent) {
                double changeX = ((double)(mouseEvent.getX() - currentX) * Math.pow(scale, 0.5)) / 5000000.0;
                double changeY = ((double)(mouseEvent.getY() - currentY) * Math.pow(scale, 0.5)) / 5000000.0;
                dragX = dragX + changeX;
                dragY = dragY + changeY;
                currentX = mouseEvent.getX();
                currentY = mouseEvent.getY();
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

        // for some reason it makes keyAdapter above work ...
        getInputMap().put(KeyStroke.getKeyStroke("A"), "");

        Button restartSimulation = new Button("Restart simulation");
        restartSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                restartSimulation();
            }
        });
        add(restartSimulation);

        animationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (isSimulating()) {
                    pauseSimulation();
                } else {
                    resumeSimulation();
                }
            }
        });
        add(animationButton);

        bodySelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                selectedBody = bodies.getBody((String)bodySelector.getSelectedItem());
            }
        });
        add(bodySelector);

    }

    SimulationPanel(double scale, Bodies<BodyMetaSwing> bodies) {

        this();

        setScale(scale);
        setBodies(bodies);

    }

    void setBodies(Bodies<BodyMetaSwing> bodies) {
        this.bodies = bodies.copy();
        this.originalBodies = bodies.copy();
        updateBodySelector(bodies);
    }

    private void updateBodySelector(Bodies<BodyMetaSwing> bodies) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        bodies.apply((Body<BodyMetaSwing> body) -> model.addElement(body.getName()));
        bodySelector.setModel(model);
        bodySelector.setSelectedItem(bodies.getHeaviestBody().getName());
    }

    void setScale(double scale) {
        this.scale = scale;
    }

    boolean isSimulating() {
        return isSimulating;
    }

    void startSimulation(Consumer<Bodies<BodyMetaSwing>> frameUpdate) {
        pauseSimulation();
        // Animate. Does repaint ~60 times a second.
        timer = new Timer(16, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(frameUpdate != null && isSimulating()) frameUpdate.accept(bodies);
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
        animationButton.setLabel("Pause simulation");
    }

    void pauseSimulation() {
        isSimulating = false;
        animationButton.setLabel("Resume simulation");
    }

    public void paintComponent(Graphics g) {

        // See: https://stackoverflow.com/a/13281121/893222
        // without it panel get visual artifacts after repaint
        super.paintComponent(g);

        turnAntialiasingOn(g);

        drawBodies(g);

    }

    private void drawBodies(Graphics g) {

        Bodies<BodyMetaSwing> displayBodies = bodies.copy();

        displayBodies.apply(
            (Body<BodyMetaSwing> body) -> {

                Vector nullVector = new Vector();

                body.setPosition(body.getPosition().sum(selectedBody.getPosition().product(-1)));

                body.setPosition(body.getPosition().product(1 / scale));

                body.setPosition(body.getPosition().rotateAroundAxisX(nullVector, dragY));

                body.setPosition(body.getPosition().rotateAroundAxisY(nullVector, dragX));

                body.setPosition(body.getPosition().sum(new Vector(translationX, translationY)));

            }
        );

        Color oldColor = g.getColor();
        for(Body<BodyMetaSwing> body: displayBodies.getBodies()) {

            Vector vector = body.getPosition();

            int x = (int)Math.round(vector.x) + getWidth() / 2;
            int y = (int)Math.round(vector.y) + getHeight() / 2;

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
        Cube cube = new Cube(new Vector(getWidth() / 2 -200, getHeight() / 2 -200, -200), 400);
        cube.rotateAroundAxisX(new Vector(getWidth() / 2, getHeight() / 2), dragY);
        cube.rotateAroundAxisY(new Vector(getWidth() / 2, getHeight() / 2), dragX);

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
