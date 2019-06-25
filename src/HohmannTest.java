import ControlSystem.Controller;
import ControlSystem.Controllers.*;
import ODESolvers.LeapfrogODE;
import Optimization.HillDescent;
import Optimization.Setup;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.SolarSystem.Titan;
import Simulation.*;
import Simulation.Spacecrafts.Starship;
import Utilities.FileSystem;
import Utilities.Units;
import Utilities.Utils;
import Visualisation.Simulation;

import java.util.Random;
import java.util.function.Function;

@SuppressWarnings("Duplicates")
public class HohmannTest {

    private static Simulation simulation;

    private static final double timeStep = 2000.0; // in s
    private static final long steps = (long)(7*365*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)Math.max(1, 24*60*60 / timeStep);

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("planets-mini-01-May-2024.txt"));

        Body sun = bodies.getBody("Sun");
        Body earth = bodies.getBody("Earth");
        Body saturn = bodies.getBody("Saturn");
        Body titan = bodies.getBody("Titan");
        bodies.removeBody("Titan");
        bodies.addBody(new Titan());
        bodies.getBody("Titan").addPosition(titan.getPosition());
        bodies.getBody("Titan").addVelocity(titan.getVelocity());

        System.out.println("Steps:" + steps);
        System.out.println("Orbital velocity: " + earth.computeOrbitalSpeed(300*1000));

        while(true) {
            new LeapfrogODE().iterate(bodies, 10);
            double earthAngle = Utils.clockAngle(earth.getPosition().x, earth.getPosition().y);
            double saturnAngle = Utils.clockAngle(saturn.getPosition().x, saturn.getPosition().y);

            double relativeAngle = Math.toDegrees(Utils.getSignedDistanceBetweenAngles(earthAngle, saturnAngle));
            relativeAngle = Math.abs(relativeAngle);

            if(relativeAngle < 106.85) break; // 106.5 && 15.0 (fuel) || 106.85 && 16.32 -> 2.35 y (speed)
        }


        double orbitHeight = 300 * 1000;

        Vector directionFromEarthToSun = earth.getRelativePosition(sun).unitVector();
        Vector earthVelocityDirection = earth.getVelocity().unitVector();

        Bodies startPrototypes = new Bodies();

        final double earthSpeed = earth.getVelocity().getLength();

        Spacecraft startPrototype = new Starship("Starship", "Titan", new NullController());
        startPrototype.addVelocity(earth.getVelocity().sum(earthVelocityDirection.product(16.32 * 1000)));
        startPrototype.addPosition(
            earth.getPosition().sum(directionFromEarthToSun.product(earth.getRadius() + orbitHeight))
        );

        startPrototypes.addBody(startPrototype);

        class Box { public Spacecraft v; }
        final Box box1 = new Box();
        final Box box2 = new Box();
        box1.v = startPrototype;
        box2.v = startPrototype;

        final Random random = new Random();

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

                Vector speedUpdate = box1.v.getVelocity().difference(box2.v.getVelocity());

                Spacecraft currentPrototype = prototype.copy();
                currentPrototype.addVelocity(speedUpdate);
                currentPrototype.rename("" + prototypes.getBodiesCount());
                currentPrototype.addPosition(new Vector(prototypes.getBodiesCount() * (prototype.getRadius() + 100), 0));
                if(currentPrototype.getVelocity().getLength() <= startPrototype.getVelocity().getLength()) {
                    prototypes.addBody(currentPrototype);
                }

                currentPrototype = prototype.copy();
                currentPrototype.addVelocity(speedUpdate.product(1/2));
                currentPrototype.rename("" + prototypes.getBodiesCount());
                currentPrototype.addPosition(new Vector(prototypes.getBodiesCount() * (prototype.getRadius() + 100), 0));
                if(currentPrototype.getVelocity().getLength() <= startPrototype.getVelocity().getLength()) {
                    prototypes.addBody(currentPrototype);
                }

                currentPrototype = prototype.copy();
                currentPrototype.addVelocity(speedUpdate.product(2));
                currentPrototype.rename("" + prototypes.getBodiesCount());
                currentPrototype.addPosition(new Vector(prototypes.getBodiesCount() * (prototype.getRadius() + 100), 0));
                if(currentPrototype.getVelocity().getLength() <= startPrototype.getVelocity().getLength()) {
                    prototypes.addBody(currentPrototype);
                }

                currentPrototype = prototype.copy();
                currentPrototype.addVelocity(speedUpdate.product(4));
                currentPrototype.rename("" + prototypes.getBodiesCount());
                currentPrototype.addPosition(new Vector(prototypes.getBodiesCount() * (prototype.getRadius() + 100), 0));
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
                        directionFromEarthToSun.product((Math.random() - 0.5) * 100)
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

        Setup bestSetup = hillDescent.getBestSetup();

        simulation = new Simulation(bestSetup.getAllBodies(), steps, timeStep, stepsPerFrame, 0.4e10);

        double bestFitness = hillDescent.getBestFitness();
        while (bestFitness > 0.001) {

            bestSetup = hillDescent.optimizationStep();

            if(bestFitness > hillDescent.getBestFitness()) {

                box2.v = box1.v;
                box1.v = bestSetup.getBestPrototype();

                bestFitness = hillDescent.getBestFitness();
                System.out.println("New best fitness: " + Units.distance(bestFitness));
                System.out.println("Speed: " + (bestSetup.getBestPrototype().getVelocity().getLength() - earthSpeed) + " " + bestSetup.getBestPrototype().getName());

                simulation.setBodies(bestSetup.getAllBodies());

            } else {
                System.out.println(Units.distance(hillDescent.getBestFitness()));
            }

        }

        simulation.save("/home/isinlor/Projects/TitanMission/resources", "Earth-Titan.txt");

        System.exit(0);

    }

}
