import ODESolvers.LeapfrogODE;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Constants;
import Simulation.Vector;
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

    private static String resourcesPath;

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        resourcesPath = args[0];
        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("planets.txt"));
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

        earth.setMass(1);
        earth.addVelocity(earth.getVelocity().unitVector().product(10.28 * 1000));

        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 0.4e10);

    }

}
