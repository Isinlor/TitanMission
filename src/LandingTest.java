import ControlSystem.Controllers.*;
import EventSystem.Event;
import Utilities.FileSystem;
import Visualisation.Simulation;

import Simulation.*;
import Utilities.*;

import java.util.function.Function;

public class LandingTest {

    private static Simulation simulation;

    private static final double timeStep = 5.0; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(5 / timeStep);

    public static void main(String[] args) {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("titan.txt"));
        Body titan = bodies.getBody("Titan");

        // Titan atmosphere: https://solarsystem.nasa.gov/moons/saturn-moons/titan/in-depth/#atmosphere_otp
        double probeAltitude = 700 * 1000; // 100km above atmosphere, in order to avoid atmosphere influence
        double probeOrbitalSpeed = titan.computeOrbitalSpeed(probeAltitude);

        // initialize functions that will smoothly switch control between different controllers
        // one controller will maintain constant angle to the spacecraft velocity, allowing to deorbit efficiently
        // the other controller will maintain constant angle to the surface, allowing to reduce speed of approach
        Function<Spacecraft, Double> importantAtSurface = (Spacecraft spacecraft) -> {
            double distanceToCenter = spacecraft.getPosition().euclideanDistance(titan.getPosition());
            double distanceToSurface = distanceToCenter - titan.getRadius();
            return 1. - distanceToSurface / probeAltitude;
        };

        Function<Spacecraft, Double> importantInOrbit = (Spacecraft spacecraft) -> {
            double distanceToCenter = spacecraft.getPosition().euclideanDistance(titan.getPosition());
            double distanceToSurface = distanceToCenter - titan.getRadius();
            return distanceToSurface / probeAltitude;
        };

        bodies.addBody(new Spacecraft(
            "Spacecraft",
            titan,
//            new NullController(),
            new CompositeController(
                new KeyboardController(),
                // FIXME: this controller somehow contributes to simulation breakdown when orbital speed is 0
                WeightedController.createConstantThrustWeightController(
                    RotationController.createMaintainAngleToVelocityController(Math.PI),
                    1,
                    importantInOrbit
                ),
                WeightedController.createConstantThrustWeightController(
                    RotationController.createMaintainAngleToSurfaceController(titan,0),
                    1,
                    importantAtSurface
                ),
                new SuicideBurnController(titan, 10000)
            ),
            new Vector(titan.getRadius() + probeAltitude, 0, 0), new Vector(0, 0, Math.PI),
            new Vector(0, probeOrbitalSpeed, 0), new Vector(0, 0, 0.00),
            1, 1, new Metadata()
        ));

        bodies.addEventListener("body crashed", (Event event) -> {
            BodyCrashedEvent crashedEvent = (BodyCrashedEvent) event;
            Body crashedBody = crashedEvent.getCrashedBody();
            System.out.println(
                "Vertical speed: " + crashedBody.getApproachSpeed(titan) + ", Landing velocity: " + crashedBody.getVelocity().getLength() + ", " + crashedBody
            );
        });

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

}
