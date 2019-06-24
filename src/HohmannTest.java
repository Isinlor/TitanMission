import ControlSystem.Controller;
import ControlSystem.Controllers.*;
import ODESolvers.LeapfrogODE;
import Optimization.HillDescent;
import Optimization.Setup;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.SolarSystem.Titan;
import Simulation.*;
import Utilities.FileSystem;
import Utilities.Units;
import Utilities.Utils;
import Visualisation.Simulation;

import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class HohmannTest {

    private static Simulation simulation;

    private static final double timeStep = 2000.0; // in s
    private static final long steps = (long)(5*365*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)Math.max(1, 24*60*60 / timeStep);

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("planets-mini.txt"));

        Body sun = bodies.getBody("Sun");
        Body earth = bodies.getBody("Earth");
        Body saturn = bodies.getBody("Saturn");
        Body titan = bodies.getBody("Titan");
        bodies.removeBody("Titan");
        bodies.addBody(new Titan());
        bodies.getBody("Titan").addPosition(titan.getPosition());
        bodies.getBody("Titan").addVelocity(titan.getVelocity());

        System.out.println("Steps:" + steps);

        for (int i = 0; i < 100; i++) {
            new LeapfrogODE().iterate(bodies, timeStep);
            double earthAngle = Utils.clockAngle(earth.getPosition().x, earth.getPosition().y);
            double saturnAngle = Utils.clockAngle(saturn.getPosition().x, saturn.getPosition().y);

            double relativeAngle = Math.toDegrees(Utils.getSignedDistanceBetweenAngles(earthAngle, saturnAngle));
            relativeAngle = Math.abs(relativeAngle);

            if(relativeAngle < 106.5) break;
        }

        Controller controller =
            WeightedController.createStartAtAltitudeController(
                new CompositeController(
                    new SuicideBurnController(100000),
                    RotationController.createMaintainAngleToSurfaceController(Math.PI)
                )
              ,
              1000000
            );


        double orbitHeight = 300 * 1000;

        Vector directionFromEarthToSun = earth.getRelativePosition(sun).unitVector();
        Vector earthVelocityDirection = earth.getVelocity().unitVector();

        Bodies startPrototypes = new Bodies();

        Spacecraft startPrototype = new Spacecraft("Spacecraft", "Titan", controller);
        startPrototype.addVelocity(earth.getVelocity().sum(earthVelocityDirection.product(17 * 1000)));
        startPrototype.addPosition(
            earth.getPosition().sum(directionFromEarthToSun.product(earth.getRadius() + orbitHeight))
        );

        startPrototypes.addBody(startPrototype);

        class Box { public Spacecraft v; }
        final Box box1 = new Box();
        final Box box2 = new Box();
        box1.v = startPrototype;
        box2.v = startPrototype;

        HillDescent hillDescent = new HillDescent(
            steps,
            timeStep,
            new LeapfrogODE(),
            new Setup(
                startPrototype,
                startPrototypes,
                bodies
            ),
            (Setup setup) -> {

                Spacecraft prototype = setup.getBestPrototype();
                prototype.setBodies(null);

                Bodies prototypes = new Bodies();

                prototype.rename("" + prototypes.getBodiesCount());
                prototypes.addBody(prototype);

                Spacecraft currentPrototype = prototype.copy();
                currentPrototype.addVelocity(box1.v.getVelocity().difference(box2.v.getVelocity()));
                currentPrototype.rename("" + prototypes.getBodiesCount());
                if(currentPrototype.getVelocity().getLength() <= startPrototype.getVelocity().getLength()) {
                    prototypes.addBody(currentPrototype);
                }

                while (prototypes.getBodiesCount() < 15) {

                    currentPrototype = prototype.copy();

                    Vector adjustment = earthVelocityDirection.product(Math.random() - 0.5).product(Math.pow((prototypes.getBodiesCount() / 2.) * prototypes.getBodiesCount() * Math.random(), Math.random() + 0.5));
                    currentPrototype.addVelocity(adjustment);

                    if(currentPrototype.getVelocity().getLength() > startPrototype.getVelocity().getLength()) {
                        continue;
                    }

                    currentPrototype.addPosition(
                        directionFromEarthToSun.product((Math.random() - 0.5) * 20)
                    );

                    currentPrototype.rename("" + prototypes.getBodiesCount());

                    prototypes.addBody(currentPrototype);

                }

                return setup.withNewPrototypes(prototypes);

            },
            (Spacecraft spacecraft) -> {
                return spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());
            }
        );

        double bestFitness = hillDescent.getBestFitness();
        while (bestFitness > 0.001) {

            Setup newSetup = hillDescent.optimizationStep();

            if(bestFitness > hillDescent.getBestFitness()) {

                box2.v = box1.v;
                box1.v = newSetup.getBestPrototype();

                bestFitness = hillDescent.getBestFitness();
                System.out.println("New best fitness: " + Units.distance(bestFitness));
                System.out.println("Speed: " + newSetup.getBestPrototype().getVelocity().getLength());
                simulation = new Simulation(newSetup.getAllBodies(), steps, timeStep, stepsPerFrame, 0.4e10);

            } else {
                System.out.println(Units.distance(hillDescent.getBestFitness()));
            }

        }

        System.exit(0);

    }

}
