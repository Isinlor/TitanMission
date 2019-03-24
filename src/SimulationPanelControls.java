import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;

class SimulationPanelControls extends JPanel {

    private SimulationPanel simulationPanel;

    private Button animationButton = new Button("Pause simulation");

    private JComboBox<String> bodySelector = new JComboBox<>();

    SimulationPanelControls(SimulationPanel simulationPanel) {

        setBackground(Color.WHITE);

        this.simulationPanel = simulationPanel;

        MouseAdapter mouseAdapter = new MouseInputAdapter() {
            int currentX;
            int currentY;
            public void mousePressed(MouseEvent mouseEvent) {
                currentX = mouseEvent.getX();
                currentY = mouseEvent.getY();
            }
            public void mouseDragged(MouseEvent mouseEvent) {
                double changeX = ((double)(mouseEvent.getX() - currentX) * Math.pow(simulationPanel.scale, 0.5)) / 5000000.0;
                double changeY = ((double)(mouseEvent.getY() - currentY) * Math.pow(simulationPanel.scale, 0.5)) / 5000000.0;
                simulationPanel.dragX = simulationPanel.dragX + changeX;
                simulationPanel.dragY = simulationPanel.dragY + changeY;
                currentX = mouseEvent.getX();
                currentY = mouseEvent.getY();
            }
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                simulationPanel.scale = simulationPanel.scale * Math.pow(1.08, mouseWheelEvent.getWheelRotation());
            }
        };

        simulationPanel.addMouseListener(mouseAdapter);
        simulationPanel.addMouseMotionListener(mouseAdapter);
        simulationPanel.addMouseWheelListener(mouseAdapter);

        KeyAdapter keyAdapter = new KeyAdapter() {
            public void keyPressed(KeyEvent keyEvent) {
                int step = -3;
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.VK_UP:
                    case KeyEvent.VK_W:
                        simulationPanel.translationY += step;
                        break;
                    case KeyEvent.VK_DOWN:
                    case KeyEvent.VK_S:
                        simulationPanel.translationY -= step;
                        break;
                    case KeyEvent.VK_LEFT:
                    case KeyEvent.VK_A:
                        simulationPanel.translationX += step;
                        break;
                    case KeyEvent.VK_RIGHT:
                    case KeyEvent.VK_D:
                        simulationPanel.translationX -= step;
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
                simulationPanel.restartSimulation();
            }
        });
        add(restartSimulation);

        animationButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (simulationPanel.isSimulating()) {
                    pauseSimulation();
                } else {
                    resumeSimulation();
                }
            }
        });
        add(animationButton);

        bodySelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                simulationPanel.selectedBody = simulationPanel.bodies.getBody((String)bodySelector.getSelectedItem());
            }
        });
        add(bodySelector);

    }

    void resumeSimulation() {
        simulationPanel.resumeSimulation();
        animationButton.setLabel("Pause simulation");
    }

    void pauseSimulation() {
        simulationPanel.pauseSimulation();
        animationButton.setLabel("Resume simulation");
    }

    void updateBodySelector() {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
        simulationPanel.bodies.apply((Body body) -> model.addElement(body.getName()));
        bodySelector.setModel(model);
        if(simulationPanel.selectedBody != null && simulationPanel.bodies.hasBody(simulationPanel.selectedBody.getName())) {
            // keep selected body if exists
            bodySelector.setSelectedItem(simulationPanel.selectedBody.getName());
        } else {
            bodySelector.setSelectedItem(simulationPanel.bodies.getHeaviestBody().getName());
        }
    }

}
