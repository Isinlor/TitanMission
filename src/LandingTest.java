import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.Controllers.KeyboardController;
import ControlSystem.Controllers.NullController;
import EventSystem.Event;
import Utilities.FileSystem;
import Visualisation.Simulation;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

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

        Body orbitingProbe = new Spacecraft(
            "Spacecraft", new KeyboardController(),
            new Vector(titan.getRadius() + probeAltitude, 0, 0), new Vector(0, 0, Math.PI),
            new Vector(0, probeOrbitalSpeed, 0), new Vector(0, 0, 0.00),
            1, 1, new Metadata()
        );

        bodies.addBody(orbitingProbe);

        bodies.addEventListener("body crashed", (Event event) -> {
            BodyCrashedEvent crashedEvent = (BodyCrashedEvent) event;
            System.out.println(
                "Landing velocity: " + crashedEvent.getCrashedBody().getVelocity().getLength() + ", " + crashedEvent.getCrashedBody()
            );
        });

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

}
