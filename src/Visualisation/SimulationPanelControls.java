package Visualisation;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class SimulationPanelControls extends JPanel {

    private SimulationPanel simulationPanel;

    private Button animationButton = new Button("Pause simulation");

    private JComboBox<String> bodySelector = new JComboBox<>();

    private JSpinner stepsPerFrameSpinner;
    private Consumer<Integer> stepsPerFrameChangeListener = (i) -> {};

    private JSpinner stepSpinner;
    private Consumer<Double> stepChangeListener = (d) -> {};

    public SimulationPanelControls(SimulationPanel simulationPanel) {

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

        Button restartSimulation = new Button("Restart simulation");
        restartSimulation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                simulationPanel.restartSimulation();
                updateBodySelector();
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
                String selectedItem = (String) bodySelector.getSelectedItem();
                if(simulationPanel.bodies.hasBody(selectedItem)) {
                    simulationPanel.selectedBody = simulationPanel.bodies.getBody(selectedItem);
                } else {
                    updateBodySelector();
                }
            }
        });
        add(bodySelector);

        add(new JLabel("SPF: "));
        stepsPerFrameSpinner = new JSpinner();
        stepsPerFrameSpinner.setPreferredSize(new Dimension(60, 24));
        stepsPerFrameSpinner.addChangeListener((e) -> {
            stepsPerFrameChangeListener.accept(((Double)stepsPerFrameSpinner.getValue()).intValue());
        });
        add(stepsPerFrameSpinner);

        add(new JLabel("Step: "));
        stepSpinner = new JSpinner();
        stepSpinner.setPreferredSize(new Dimension(60, 24));
        stepSpinner.addChangeListener((e) -> {
            stepChangeListener.accept((Double)stepSpinner.getValue());
        });
        add(stepSpinner);

    }

    public void resumeSimulation() {
        simulationPanel.resumeSimulation();
        animationButton.setLabel("Pause simulation");
    }

    public void pauseSimulation() {
        simulationPanel.pauseSimulation();
        animationButton.setLabel("Resume simulation");
    }

    public void updateBodySelector() {
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

    void setStepsPerFrameSpinnerControl(SpinnerNumberModel model, Consumer<Integer> changeListener) {
        stepsPerFrameSpinner.setModel(model);
        stepsPerFrameChangeListener = changeListener;
    }

    void setStepSpinnerControl(SpinnerNumberModel model, Consumer<Double> changeListener) {
        stepSpinner.setModel(model);
        stepChangeListener = changeListener;
    }

}
