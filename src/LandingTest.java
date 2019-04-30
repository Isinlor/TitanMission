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

        Body orbitingProbe = new Body("probe A", new Vector(titan.getRadius() + probeAltitude, 0, 0), new Vector(0, probeOrbitalSpeed, 0), 1);
        Body landingProbe  = orbitingProbe.copy();
        landingProbe.rename("probe B");
        bodies.addBody(orbitingProbe);
        bodies.addBody(landingProbe);

        bodies.addEventListener("body crashed", (Event event) -> {
            BodyCrashedEvent crashedEvent = (BodyCrashedEvent) event;
            System.out.println("Crashed body -- " + crashedEvent.getCrashedBody());
        });

        landingProbe.addVelocity(new Vector(0, -probeOrbitalSpeed/2, 0));

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 1e4);

    }

}
