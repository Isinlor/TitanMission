import javax.swing.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Simulation {

    static private JFrame window = new JFrame();
    static private SimulationPanel simulationPanel = new SimulationPanel();
    static private SimulationPanelControls simulationPanelControls = new SimulationPanelControls(simulationPanel);

    private Bodies bodies;

    private ODESolver odeSolver = new EulerODE();
    // not tested yet
    // private ODESolver odeSolver = new RungaKuttaODE();

    private long steps;
    private double timeStep;

    private long stepsPerFrame;
    private double scale;

    private Metadata metadata = new Metadata();

    private long replied;

    static {
        System.setProperty("sun.java2d.opengl", "true");
        window.getContentPane().setLayout(new BorderLayout());
        window.getContentPane().add(simulationPanel, BorderLayout.CENTER);
        window.getContentPane().add(simulationPanelControls, BorderLayout.NORTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        load(args[0]).start();
    }

    Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale) {
        setODESolver(odeSolver);
        setBodies(bodies);
        this.steps = steps;
        this.timeStep = timeStep;
        this.stepsPerFrame = stepsPerFrame;
        this.scale = scale;
        simulationPanel.setScale(scale);
        start();
    }

    Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale, Metadata metadata) {
        this(bodies, steps, timeStep, stepsPerFrame, scale);
        this.metadata = metadata;
        if(metadata.has("ODESolver")) {
            setODESolver(ODESolvers.getODESolver(metadata.get("ODESolver")));
        }
    }

    void setODESolver(ODESolver odeSolver) {
        this.odeSolver = odeSolver;
        metadata.set("ODESolver", odeSolver.getName());
    }

    private void start() {

        window.pack();
        window.setVisible(true);

        simulationPanel.startSimulation(
            (Bodies bodies) -> {

                if(replied == steps) {
                    pause();
                }

                for (int i = 0; i < stepsPerFrame; i++) {
                    odeSolver.iterate(bodies, timeStep);
                    replied++;
                }

            }
        );

    }

    private void pause() {
        simulationPanelControls.pauseSimulation();
    }

    private void resume() {
        simulationPanelControls.resumeSimulation();
    }

    void setBodies(Bodies bodies) {
        this.replied = 0;
        this.bodies = bodies.copy();
        simulationPanel.setBodies(this.bodies.copy());
        simulationPanelControls.updateBodySelector();
        resume();
    }

    Bodies getBodies() {
        return bodies;
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

    double getScale() {
        return scale;
    }

    Metadata getMetadata() {
        return metadata;
    }

    void save(String resource) {
        FileSystem.write(resource, serialize());
    }

    static Simulation load(String resource) {
        try {
            return unserialize(FileSystem.read(resource));
        } catch (Exception e) {
            throw new RuntimeException("\n\tFailed to unserialize simulation from resource " + resource, e);
        }
    }

    String serialize() {
        return "" +
            "steps(" + steps + ") " +
            "timeStep(" + timeStep + ") " +
            "stepsPerFrame(" + stepsPerFrame + ") " +
            "scale(" + scale + ") " +
            "metadata(" + metadata + ")\n" +
            bodies.serialize();
    }

    static Simulation unserialize(String string) {

        Pattern pattern = Pattern.compile("" +
            "steps\\((?<steps>.+)\\) " +
            "timeStep\\((?<timeStep>.+)\\) " +
            "stepsPerFrame\\((?<stepsPerFrame>.+)\\) " +
            "scale\\((?<scale>.+)\\) " +
            "metadata\\((?<metadata>.+)\\)"
        );
        Matcher matcher = pattern.matcher(string.split("(\\r\\n|\\r|\\n)")[0].trim());

        if(!matcher.matches()) {
            throw new RuntimeException("\n\tWrong format, can not unserialize simulation!");
        }

        return new Simulation(
            Bodies.unserialize(string.split("(\\r\\n|\\r|\\n)", 2)[1].trim()),
            Long.parseLong(matcher.group("steps")),
            Double.parseDouble(matcher.group("timeStep")),
            Long.parseLong(matcher.group("stepsPerFrame")),
            Double.parseDouble(matcher.group("scale")),
            Metadata.unserialize(matcher.group("metadata"))
        );

    }

}
