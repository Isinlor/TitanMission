import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.Controllers.*;
import ODESolvers.LeapfrogODE;
import Utilities.FileSystem;
import Visualisation.Simulation;

import Simulation.*;
import Utilities.*;

public class LandingTest {

    private static Simulation simulation;

    private static final double timeStep = 5.0; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(5 / timeStep);

    public static void main(String[] args) {

        simpleDestinationTest();
//        simpleLandingTest();
//        replayControllerLandingTest();

    }

    static void simpleDestinationTest() {

        Bodies bodies = new Bodies();

        Spacecraft b = new Spacecraft("B", "A", new NullController());

        Spacecraft a = new Spacecraft("A", "B", new DestinationController(1));
        a.addPosition(new Vector(100, 0));
        a.addVelocity(new Vector(10, 10));

        bodies.addBody(a);
        bodies.addBody(b);

        simulation = new Simulation(bodies, steps, 0.01, 10, 0.3);
    }

    static void simpleLandingTest() {
        Bodies actual = createSimulation(new CompositeController(
            RotationController.createMaintainAngleToRelativeVelocityController(0),
            new SuicideBurnController(701000)
        ));

        simulation = new Simulation(actual, steps, timeStep, stepsPerFrame, 1e4);
    }

    static void replayControllerLandingTest() {

        RecordController<Command> controller = RecordController.createCommandRecordController(new CompositeController(
            RotationController.createMaintainAngleToRelativeVelocityController(0),
            new SuicideBurnController(701000)
        ));

        Bodies predictions = createSimulation(controller);
        while(predictions.getBody("Spacecraft") != null) {
            new LeapfrogODE().iterate(predictions, timeStep);
        }
//        controller.getRecording().save("/home/isinlor/Projects/TitanMission/resources/recording.txt");

        Bodies actual = createSimulation(new ReplayController(controller.getRecording()));

        simulation = new Simulation(actual, steps, timeStep, stepsPerFrame, 1e4);

    }

    static Bodies createSimulation(Controller controller) {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        bodies.addBody(new Spacecraft(
            "Spacecraft",
            titan.getName(),
            controller,
            new Vector(titan.getRadius() + probeAltitude, 0, 0), new Vector(0, 0, 0),
            new Vector(0, probeOrbitalSpeed, 0), new Vector(0, 0, 0.00),
            1, 1, new Metadata()
        ));

        return bodies;
    }

}
