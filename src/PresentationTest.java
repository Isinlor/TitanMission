import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.Controllers.DestinationController;
import ControlSystem.Controllers.RecordController;
import ControlSystem.Controllers.ReplayController;
import EffectSystem.EffectSystem;
import EffectSystem.Types.*;
import ODESolvers.LeapfrogODE;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Spacecraft;
import Simulation.Vector;
import Utilities.FileSystem;
import Utilities.Utils;
import Visualisation.Simulation;

@SuppressWarnings({"Duplicates"})
public class PresentationTest {

    private static Simulation simulation;

    private static final double timeStep = 0.05; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(5 / timeStep);

    public static void main(String[] args) {
        destinationStressTest();
//        singleProbeDestinationTest();
//        singleProbeDestinationStatisticsTest();
    }

    static void destinationStressTest() {

        Bodies bodies = new Bodies();

        Body b = new Body("Target", new Vector(), new Vector(), 200000, 10);
        bodies.addBody(b);

        int x = 0;
        Vector standardVector = new Vector(1, 1);
        for (int i = 1; i <= 8; i++) {

            double rotationPosition = (Utils.TAU / 8) * i + 0.01;

            for (int j = 1; j <= 8; j++) {

                double rotationVelocity = (Utils.TAU / 8) * j + 0.01;

                for (int k = 1; k <= 1; k++) {

                    for (int l = 1; l <= 1; l++) {

                        Spacecraft a = new Spacecraft("" + x, "Target", new DestinationController( Math.pow(1000 + Math.random(), k - 0.8) ), 1, 0.0001);
                        a.addPosition(standardVector.rotateAroundAxisZ(new Vector(), rotationPosition).product(Math.pow(100, k)));
                        a.addVelocity(standardVector.rotateAroundAxisZ(new Vector(), rotationVelocity).product(Math.pow(10, l)));
                        a.setInfo(false);

                        bodies.addBody(a);

                        x++;

                    }

                }

            }



        }

        simulation = new Simulation(bodies, steps, 0.01, 1, 0.45);

    }

    static void singleProbeDestinationTest() {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        double spacecrafts = 30;
        double spacing = -Math.PI / 2 / (spacecrafts - 1);
        for (int i = 0; i < spacecrafts; i++) {
            if(12 != i) continue;
            double theta = spacing * i;
            Body pointTarget = new Body("Landing spot", new Vector(titan.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
            pointTarget.getMeta().set("noEffects", "true");
            Controller controller = DestinationController.createWithStaticTarget(pointTarget, 150000);

            bodies.addBody(pointTarget);

//            controller = new DestinationController(10000);

            Spacecraft a = new Spacecraft("Spacecraft", "Titan", controller, 10000, 1.5);
            a.addPosition(new Vector(titan.getRadius() + probeAltitude,0).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));
            a.addVelocity(new Vector(0, probeOrbitalSpeed).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));

            a.setInfo(true);

            a.getMeta().set("x", "" + pointTarget.getPosition().x);
            a.getMeta().set("y", "" + pointTarget.getPosition().y);

            bodies.addBody(a);
        }

        simulation = new Simulation(bodies, steps, 0.01, 50, 0.7e4);

    }

    static void singleProbeDestinationStatisticsTest() {

        EffectSystem withoutWind = new CompositeEffectSystem(
            new CollisionEffectSystem(),
            new GravitationalEffectSystem(),
            new ControllerEffectSystem()
        );

        EffectSystem withWind = new CompositeEffectSystem(
            new CollisionEffectSystem(),
            new GravitationalEffectSystem(),
            new ControllerEffectSystem(),
            new AtmosphereEffectSystem()
        );

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        RecordController<Command> recordController;

        double spacecrafts = 30;
        double spacing = -Math.PI / 2 / (spacecrafts - 1);
//        for (int i = 0; i < spacecrafts; i++) {
//            if(12 != i) continue;
        double theta = spacing * 12;
        Body pointTarget = new Body("Landing spot", new Vector(titan.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
        pointTarget.getMeta().set("noEffects", "true");
        Controller controller = DestinationController.createWithStaticTarget(pointTarget, 150000);

        bodies.addBody(pointTarget);

//            controller = new DestinationController(10000);

        recordController = RecordController.createCommandRecordController(controller);
        controller = recordController;

        Spacecraft a = new Spacecraft("Spacecraft", "Titan", controller, 10000, 1.5);
        a.addPosition(new Vector(titan.getRadius() + probeAltitude,0).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));
        a.addVelocity(new Vector(0, probeOrbitalSpeed).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));

        a.setInfo(true);

        a.getMeta().set("x", "" + pointTarget.getPosition().x);
        a.getMeta().set("y", "" + pointTarget.getPosition().y);

        bodies.addBody(a);

        while (bodies.getBodiesCount() > 2) {
            new LeapfrogODE(withoutWind).iterate(bodies, 0.01);
        }

        controller = new ReplayController(recordController.getRecording());

        for (int i = 0; i < 100; i++) {
            bodies = getBodiesTesting(new ReplayController(recordController.getRecording()));
            while (bodies.getBodiesCount() > 2) {
                new LeapfrogODE(withWind).iterate(bodies, 0.01);
            }
        }

//        bodies = getBodiesTesting(new ReplayController(recordController.getRecording()));

//        simulation = new Simulation(bodies, steps, 0.01, 50, 0.7e4);

    }

    private static Bodies getBodiesTesting() {
        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        RecordController<Command> recordController;

        double spacecrafts = 30;
        double spacing = -Math.PI / 2 / (spacecrafts - 1);
//        for (int i = 0; i < spacecrafts; i++) {
//            if(12 != i) continue;
            double theta = spacing * 12;
            Body pointTarget = new Body("Landing spot", new Vector(titan.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
            pointTarget.getMeta().set("noEffects", "true");
            Controller controller = DestinationController.createWithStaticTarget(pointTarget, 150000);

            bodies.addBody(pointTarget);

//            controller = new DestinationController(10000);

            recordController = RecordController.createCommandRecordController(controller);
            controller = recordController;

            Spacecraft a = new Spacecraft("Spacecraft", "Titan", controller, 10000, 1.5);
            a.addPosition(new Vector(titan.getRadius() + probeAltitude,0).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));
            a.addVelocity(new Vector(0, probeOrbitalSpeed).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));

            a.setInfo(true);

            a.getMeta().set("x", "" + pointTarget.getPosition().x);
            a.getMeta().set("y", "" + pointTarget.getPosition().y);

            bodies.addBody(a);
//        }
        return bodies;
    }

    private static Bodies getBodiesTesting(Controller controller) {
        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        RecordController<Command> recordController;

        double spacecrafts = 30;
        double spacing = -Math.PI / 2 / (spacecrafts - 1);
//        for (int i = 0; i < spacecrafts; i++) {
//            if(12 != i) continue;
        double theta = spacing * 12;
        Body pointTarget = new Body("Landing spot", new Vector(titan.getRadius(), 0).rotateAroundAxisZ(new Vector(), theta), new Vector(), 1, 0.0000001);
        pointTarget.getMeta().set("noEffects", "true");
//        Controller controller = DestinationController.createWithStaticTarget(pointTarget, 150000);

        bodies.addBody(pointTarget);

//            controller = new DestinationController(10000);

//        recordController = RecordController.createCommandRecordController(controller);
//        controller = recordController;

        Spacecraft a = new Spacecraft("Spacecraft", "Titan", controller, 10000, 1.5);
        a.addPosition(new Vector(titan.getRadius() + probeAltitude,0).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));
        a.addVelocity(new Vector(0, probeOrbitalSpeed).rotateAroundAxisZ(new Vector(), theta + 4 * spacing));

        a.setInfo(true);

        a.getMeta().set("x", "" + pointTarget.getPosition().x);
        a.getMeta().set("y", "" + pointTarget.getPosition().y);

        bodies.addBody(a);
//        }
        return bodies;
    }

}
