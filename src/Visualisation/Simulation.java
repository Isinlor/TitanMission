package Visualisation;

import ODESolvers.*;
import ODESolvers.ODESolver;
import ODESolvers.ODESolvers;
import Simulation.Bodies;
import Utilities.FileSystem;
import Utilities.Logger.ConsoleLogger;
import Utilities.Logger.Logger;
import Utilities.Metadata;

import javax.swing.*;
import java.awt.*;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simulation {

    static final public Logger logger = new ConsoleLogger();

    static private JFrame window = new JFrame();
    static private SimulationPanel simulationPanel = new SimulationPanel();
    static private SimulationPanelControls simulationPanelControls = new SimulationPanelControls(simulationPanel);

    private Bodies bodies;

    private ODESolver odeSolver = new LeapfrogODE();

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

    public Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale) {
        this.steps = steps;
        this.timeStep = timeStep;
        this.stepsPerFrame = stepsPerFrame;
        this.scale = scale;
        setODESolver(odeSolver);
        setBodies(bodies);
        simulationPanel.setScale(scale);
        start();
    }

    public Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale, Metadata metadata) {
        this(bodies, steps, timeStep, stepsPerFrame, scale);
        this.metadata = metadata;
        if(metadata.has("ODESolver")) {
            setODESolver(ODESolvers.getODESolver(metadata.get("ODESolver")));
        }
    }

    public void setODESolver(ODESolver odeSolver) {
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

    public void setBodies(Bodies bodies) {
        this.replied = 0;
        this.bodies = bodies.copy();
        simulationPanel.setBodies(this.bodies.copy());
        simulationPanelControls.updateBodySelector();

        simulationPanelControls.setStepsPerFrameSpinnerControl(
            new SpinnerNumberModel(
                stepsPerFrame, //initial value
                1, //minimum value
                stepsPerFrame * 10, //maximum value
                1 // step
            ),
            (Integer newStepsPerFrame) -> { stepsPerFrame = newStepsPerFrame;}
        );

        simulationPanelControls.setStepSpinnerControl(
            new SpinnerNumberModel(
                timeStep, //initial value
                -timeStep, //minimum value
                timeStep * 10, //maximum value
                timeStep / 10 // step
            ),
            (Double newTimeStep) -> { timeStep = newTimeStep;}
        );

        resume();
    }

    public Bodies getBodies() {
        return bodies;
    }

    public long getSteps() {
        return steps;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public long getStepsPerFrame() {
        return stepsPerFrame;
    }

    public double getScale() {
        return scale;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void save(String location) {
        FileSystem.write(Paths.get(location), serialize());
    }

    public void save(String location, String filename) {
        FileSystem.write(Paths.get(location, filename), serialize());
    }

    public static Simulation load(String resource) {
        try {
            return unserialize(FileSystem.read(resource));
        } catch (Exception e) {
            throw new RuntimeException("\n\tFailed to unserialize simulation from resource " + resource, e);
        }
    }

    public String serialize() {
        return "" +
            "steps(" + steps + ") " +
            "timeStep(" + timeStep + ") " +
            "stepsPerFrame(" + stepsPerFrame + ") " +
            "scale(" + scale + ") " +
            "metadata(" + metadata + ")\n" +
            bodies.serialize();
    }

    public static Simulation unserialize(String string) {

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
