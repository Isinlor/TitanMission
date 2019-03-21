import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Simulation {

    static private Simulation simulation;

    static private JFrame window = new JFrame();
    static private SimulationPanel  simulationPanel = new SimulationPanel();
    static {
        System.setProperty("sun.java2d.opengl", "true");
        window.setContentPane(simulationPanel);
        window.pack();

        Button button = new Button("Restart simulation");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                simulation.start();
            }
        });
        simulationPanel.add(button);
    }

    private Bodies bodies;

    private long steps;
    private double timeStep;

    private long stepsPerFrame;
    private double scale;

    private String metadata;

    private long replied;

    public static void main(String[] args) {
        load(args[0]).start();
    }

    Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale) {
        this.bodies = bodies.copy();
        this.steps = steps;
        this.timeStep = timeStep;
        this.stepsPerFrame = stepsPerFrame;
        this.scale = scale;
        simulation = this;
    }

    Simulation(Bodies bodies, long steps, double timeStep, long stepsPerFrame, double scale, String metadata) {
        this(bodies, steps, timeStep, stepsPerFrame, scale);
        this.metadata = metadata;
    }

    void start() {

        simulationPanel.setBodies(bodies.copy());
        simulationPanel.setScale(scale);

        window.setVisible(true);

        replied = 0;
        simulationPanel.startAnimation(
            (Bodies<BodyMetaSwing> bodies) -> {

                if(replied > steps) {
                    return;
                }

                for (int i = 0; i < stepsPerFrame; i++) {
                    bodies.iterate(timeStep);
                    replied++;
                }

            }
        );

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

    String getMetadata() {
        return metadata;
    }

    Simulation withNewBodies(Bodies bodies) {
        return new Simulation(bodies, steps, timeStep, stepsPerFrame, scale, metadata);
    }

    void save(String resource) {
        FileSystem.write(resource, serialize());
    }

    static Simulation load(String resource) {
        return unserialize(FileSystem.read(resource));
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
        matcher.matches();

        return new Simulation(
            Bodies.unserialize(string.split("(\\r\\n|\\r|\\n)", 2)[1].trim()),
            Long.parseLong(matcher.group("steps")),
            Double.parseDouble(matcher.group("timeStep")),
            Long.parseLong(matcher.group("stepsPerFrame")),
            Double.parseDouble(matcher.group("scale")),
            matcher.group("metadata")
        );

    }

}
