import ControlSystem.Controllers.NullController;
import ODESolvers.LeapfrogODE;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Constants;
import Simulation.Vector;
import Simulation.*;
import Utilities.FileSystem;
import Utilities.Tuple;
import Utilities.Utils;
import Visualisation.Simulation;

@SuppressWarnings("Duplicates")
public class HohmannTest {

    private static Simulation simulation;

    private static final double timeStep = 200*60.0; // in s
    private static final long steps = (long)(600*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(24*60*60 / timeStep); // around 1 day

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("planets.txt"));

        Body sun = bodies.getBody("Sun");
        Body earth = bodies.getBody("Earth");
        Body saturn = bodies.getBody("Saturn");

        for (int i = 0; i < 100; i++) {
            new LeapfrogODE().iterate(bodies, timeStep);
            double earthAngle = Utils.clockAngle(earth.getPosition().x, earth.getPosition().y);
            double saturnAngle = Utils.clockAngle(saturn.getPosition().x, saturn.getPosition().y);

            double relativeAngle = Math.toDegrees(Utils.getSignedDistanceBetweenAngles(earthAngle, saturnAngle));
            relativeAngle = Math.abs(relativeAngle);

            System.out.println(relativeAngle);

            if(relativeAngle < 106.5) break;

        }


        Spacecraft spacecraft = new Spacecraft("Spacecraft", "Saturn", new NullController());
        bodies.addBody(spacecraft);

//        earth.setMass(1);
//        earth.addVelocity(earth.getVelocity().unitVector().product(10.3 * 1000));

        Vector directionFromEarthToSun = earth.getRelativePosition(sun).unitVector();

        spacecraft.addVelocity(earth.getVelocity().sum(earth.getVelocity().unitVector().product(15 * 1000)));
        spacecraft.addPosition(earth.getPosition().sum(directionFromEarthToSun.product(10000 * 1000)));

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 0.4e10);

    }

}
