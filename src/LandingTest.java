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

    private static final double timeStep = 0.05; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(5 / timeStep);

    public static void main(String[] args) {

        complexStaticTargetOnTitanDestinationTest();
//        complexStaticTargetDestinationTest();
//        simpleDestinationTest();
//        titanDestinationStressTest();
//        destinationStressTest();
//        subOrbitalSuicideBurnControllerTest();
//        subOrbitalDestinationControllerTest();
//        inOrbitDestinationControllerTest();
//        inOrbitReplayControllerTest();

    }

    static void complexStaticTargetOnTitanDestinationTest() {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        double spacecrafts = 4;
        double spacing = -Math.PI / 2 / (spacecrafts - 1);
        for (int i = 0; i < spacecrafts; i++) {
            double theta = spacing * i;
            Body pointTarget = new Body("t"+i, new Vector(titan.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
            pointTarget.getMeta().set("noEffects", "true");
            Controller controller = DestinationController.createWithStaticTarget(pointTarget, 1);

            bodies.addBody(pointTarget);

//            controller = new DestinationController(10000);

            Spacecraft a = new Spacecraft("" + i, "Titan", controller, 1, 1.5);
            a.addPosition(new Vector(titan.getRadius() + probeAltitude,0).rotateAroundAxisZ(new Vector(), theta + spacing + 0.0307));
            a.addVelocity(new Vector(0, probeOrbitalSpeed).rotateAroundAxisZ(new Vector(), theta + spacing + 0.0307));

            a.getMeta().set("x", "" + pointTarget.getPosition().x);
            a.getMeta().set("y", "" + pointTarget.getPosition().y);

            bodies.addBody(a);
        }

        simulation = new Simulation(bodies, steps, 0.01, 300, 1e4);

    }

    static void staticTargetDestinationTest() {

        Bodies bodies = new Bodies();

        Body b = new Body("Target", new Vector(), new Vector(), 2, 10000);

        Body pointTarget = new Body("", b.getPosition().sum(new Vector(b.getRadius(), 0).rotateAroundAxisZ(new Vector(), -Math.PI/4)), new Vector(), 1, 0.0000001);

        DestinationController controller = DestinationController.createWithStaticTarget(pointTarget, 2);

        Spacecraft a = new Spacecraft("A", "Target", controller);
        a.addPosition(new Vector(20000, -15000));
        a.addVelocity(new Vector(000, -100));

        bodies.addBody(a);
        bodies.addBody(b);

        simulation = new Simulation(bodies, steps, 0.01, 30, 100);

    }

    static void complexStaticTargetDestinationTest() {

        Bodies bodies = new Bodies();

        Body b = new Body("Target", new Vector(), new Vector(), 2, 10000);
        bodies.addBody(b);

        for (int i = 0; i < 10; i++) {
            double theta = (-Math.PI/2 / 10) * i;
            Body pointTarget = new Body("", new Vector(b.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
            DestinationController controller = DestinationController.createWithStaticTarget(pointTarget, 2);


            Spacecraft a = new Spacecraft("" + i, "Target", controller);
            a.addPosition(new Vector(20000 + i * 2, -15000 + i * 2));
            a.addVelocity(new Vector(i*10, -100));

            a.getMeta().set("x", "" + pointTarget.getPosition().x);
            a.getMeta().set("y", "" + pointTarget.getPosition().y);

            bodies.addBody(a);
        }

        simulation = new Simulation(bodies, steps, 0.01, 30, 100);

    }

    static void simpleDestinationTest() {

        Bodies bodies = new Bodies();

        Body b = new Body("Target", new Vector(), new Vector(), 2, 1);
//        b.addVelocity(new Vector(-10, -10));

        Spacecraft a = new Spacecraft("A", "Target", new DestinationController(10000), 10000, 1);
        a.addPosition(new Vector(10000, -10000));
        a.addVelocity(new Vector(000, -100));

        bodies.addBody(a);
        bodies.addBody(b);

        simulation = new Simulation(bodies, steps, 0.01, 30, 100);

    }

    static void titanDestinationStressTest() {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        int x = 0;
        Vector standardVector = new Vector(titan.getRadius() + probeAltitude, titan.getRadius() + probeAltitude);
        for (int i = 0; i < 30; i++) {

            double rotationPosition = (Utils.TAU / 30) * i;

            for (int j = 0; j < 3; j++) {

                double rotationVelocity = (Utils.TAU / 3) * j;

                Spacecraft a = new Spacecraft("" + x, "Titan", new DestinationController(1), 1, 0.001);
                a.addPosition(standardVector.rotateAroundAxisZ(new Vector(), rotationPosition).sum(new Vector(1,1).product(Math.random() * 100)));
                a.addVelocity(new Vector(1000, 1000).rotateAroundAxisZ(new Vector(), rotationVelocity));

                bodies.addBody(a);

                x++;

            }

        }

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

    static void destinationStressTest() {

        Bodies bodies = new Bodies();

        Body b = new Body("Target", new Vector(), new Vector(), 2, 1);
        bodies.addBody(b);

        int x = 0;
        Vector standardVector = new Vector(1, 1);
        for (int i = 1; i <= 10; i++) {

            double rotationPosition = (Utils.TAU / 10) * i;

            for (int j = 1; j <= 10; j++) {

                double rotationVelocity = (Utils.TAU / 10) * j;

                for (int k = 1; k <= 2; k++) {

                    for (int l = 1; l <= 2; l++) {

                        Spacecraft a = new Spacecraft("" + k + i + j + x, "Target", new DestinationController( Math.pow(1000 + Math.random(), k - 0.8) ), 1, 0.001);
                        a.addPosition(standardVector.rotateAroundAxisZ(new Vector(), rotationPosition).product(Math.pow(100, k) - 10 + i + j + l + Math.random() * 100));
                        a.addVelocity(standardVector.rotateAroundAxisZ(new Vector(), rotationVelocity).product(Math.pow(10, l)));

                        bodies.addBody(a);

                        x++;

                    }

                }

            }



        }

        simulation = new Simulation(bodies, steps, 0.01, 1, 100);

    }

    static void subOrbitalDestinationControllerTest() {

        Controller controller = new DestinationController(1);

        Bodies bodies = createSubOrbitalSimulation(controller);

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

    static void subOrbitalSuicideBurnControllerTest() {

        Controller controller = new CompositeController(
            RotationController.createMaintainAngleToRelativeVelocityController(0),
            new SuicideBurnController(701000)
        );

        Bodies bodies = createSubOrbitalSimulation(controller);

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

    static void inOrbitDestinationControllerTest() {

        DestinationController destinationController = new DestinationController(10000);
        RecordController<Command> controller = RecordController.createCommandRecordController(destinationController);

        Bodies actual = createInOrbitSimulation(controller);

//        while(actual.hasBody("Spacecraft")) {
//            new LeapfrogODE().iterate(actual, 1.5);
//        }

        controller.getRecording().save("/home/isinlor/Projects/TitanMission/resources/recording.txt");

        simulation = new Simulation(actual, steps, timeStep, stepsPerFrame, 1e4);



    }

    static void inOrbitReplayControllerTest() {

        RecordController<Command> controller = RecordController.createCommandRecordController(new CompositeController(
            RotationController.createMaintainAngleToRelativeVelocityController(0),
            new SuicideBurnController(701000)
        ));

        Bodies predictions = createInOrbitSimulation(controller);
        while(predictions.getBody("Spacecraft") != null) {
            new LeapfrogODE().iterate(predictions, timeStep);
        }
//        controller.getRecording().save("/home/isinlor/Projects/TitanMission/resources/recording.txt");

        Bodies actual = createInOrbitSimulation(new ReplayController(controller.getRecording()));

        simulation = new Simulation(actual, steps, timeStep, stepsPerFrame, 1e4);

    }

    static Bodies createInOrbitSimulation(Controller controller) {

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
            10000, 1, new Metadata()
        ));

        return bodies;

    }

    static Bodies createSubOrbitalSimulation(Controller controller) {

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
            new Vector(0, 0, 0), new Vector(0, 0, 0.00),
            1, 1, new Metadata()
        ));

        return bodies;

    }

}
