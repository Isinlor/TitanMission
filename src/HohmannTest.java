import ControlSystem.Controller;
import ControlSystem.Controllers.*;
import ODESolvers.LeapfrogODE;
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

    private static final double timeStep = 60.0; // in s
    private static final long steps = (long)(10000*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(24*60*60 / timeStep); // around 1 day

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

        for (int i = 0; i < 100; i++) {
            new LeapfrogODE().iterate(bodies, timeStep);
            double earthAngle = Utils.clockAngle(earth.getPosition().x, earth.getPosition().y);
            double saturnAngle = Utils.clockAngle(saturn.getPosition().x, saturn.getPosition().y);

            double relativeAngle = Math.toDegrees(Utils.getSignedDistanceBetweenAngles(earthAngle, saturnAngle));
            relativeAngle = Math.abs(relativeAngle);

            if(relativeAngle < 106.5) break;
        }

        Function<Spacecraft, Double> function = (Spacecraft spacecraft) -> {
            double altitude = spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());
            if(altitude < 500000) return 0.0;
            return  altitude < Units.AU ? 1. : 0.;
        };


        Controller controller = new CompositeController(
            WeightedController.createStartAtAltitudeController(
              new SuicideBurnController(1000),
              10000
            ),
            new WeightedController(
                new DestinationController(0.1),
                function,
                function
            )
        );

        Spacecraft spacecraft = new Spacecraft("Spacecraft", "Titan", controller);
        bodies.addBody(spacecraft);

//        earth.setMass(1);
//        earth.addVelocity(earth.getVelocity().unitVector().product(10.3 * 1000));

        double orbitHeight = 300 * 1000;

        Vector directionFromEarthToSun = earth.getRelativePosition(sun).unitVector();
        Vector earthVelocityDirection = earth.getVelocity().unitVector();

        spacecraft.addVelocity(earth.getVelocity().sum(earthVelocityDirection.product(15.5 * 1000)));
        spacecraft.addPosition(
            earth.getPosition().sum(directionFromEarthToSun.product(earth.getRadius() + orbitHeight))
        );

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 0.4e10);

    }

}
