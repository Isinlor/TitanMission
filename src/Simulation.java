import javax.swing.*;

class Simulation {

    static private JFrame window = new JFrame();
    static private SimulationPanel  simulationPanel = new SimulationPanel();
    static {
        System.setProperty("sun.java2d.opengl", "true");
        window.setContentPane(simulationPanel);
        window.pack();
    }

    private Bodies bodies;

    private long steps;
    private double timeStep;

    private long stepsPerFrame;
    private double scale;

    private long replied;

    Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale) {
        this.bodies = bodies.copy();
        this.steps = steps;
        this.timeStep = timeStep;
        this.stepsPerFrame = stepsPerFrame;
        this.scale = scale;
    }

    void start() {

        simulationPanel.setBodies(bodies.copy());
        simulationPanel.setScale(scale);

        window.setVisible(true);

        replied = 0;
        simulationPanel.startAnimation(
            (Bodies<BodyMetaSwing> bodies) -> {

                if(replied > steps) {
                    simulationPanel.stopAnimation();
                }

                for (int i = 0; i < stepsPerFrame; i++) {
                    bodies.iterate(timeStep);
                    replied++;
                }

            }
        );
    }

    long getSteps() {
        return steps;
    }

    double getTimeStep() {
        return timeStep;
    }

    long getStepsPerFrame() {
        return stepsPerFrame;
    }

}
