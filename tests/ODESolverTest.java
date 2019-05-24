import ODESolvers.ODESolver;
import ODESolvers.ODESolvers;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Constants;
import Simulation.Vector;
import Utilities.Utils;

public class ODESolverTest extends SimpleUnitTest {

    public static void main(String[] args) {

        for(ODESolver solver: ODESolvers.getODESolvers()) {
            System.out.println("Test " + solver.getName());
            try {
                testBasedOnSatelliteOrbit(solver);
            } catch (Exception e) {
                System.out.println("Test failed! Details:");
                e.printStackTrace();
            }
            System.out.println();
        }

    }

    /**
     * This test runs a practical simulation of a satellite in a circular orbit around Earth.
     * The satellite is do one full orbit and return to the same position.
     *
     * There are two test cases A and B. The test case B runs twice as many steps as the test case A.
     *
     * https://en.wikipedia.org/wiki/Orbital_speed#Mean_orbital_speed
     * https://en.wikipedia.org/wiki/Orbital_period#Small_body_orbiting_a_central_body
     *
     * @param solver The solver to test.
     */
    static void testBasedOnSatelliteOrbit(ODESolver solver) {

        // https://en.wikipedia.org/wiki/Earth_radius
        // https://en.wikipedia.org/wiki/Earth_mass
        double earthRadius = 6371008.0;
        Body earth = new Body("Earth", new Vector(), new Vector(), 5.9722e24, 1);
        // Notice! Earth radius is set to 1 in the simulation in order to avoid collision of satellite with the Earth in
        //         the case of bad simulation.

        double altitude = 500 * 1000;
        double semiMajorAxis = earthRadius + altitude;


        // https://en.wikipedia.org/wiki/Orbital_speed#Mean_orbital_speed
        double orbitalSpeed = Math.sqrt(Constants.G * earth.getMass() / semiMajorAxis);

        // https://en.wikipedia.org/wiki/Orbital_period#Small_body_orbiting_a_central_body
        double orbitalPeriod = Utils.TAU * Math.sqrt(Math.pow(semiMajorAxis, 3) / (Constants.G * earth.getMass()));

        Body satellite = new Body(
            "Satellite",
            new Vector(semiMajorAxis, 0,0),
            new Vector(0, orbitalSpeed, 0),
            1, 1
        );

        Bodies bodies = new Bodies();
        bodies.addBody(earth);
        bodies.addBody(satellite);



        int stepsA = 100;
        int stepsB = stepsA * 2;

        Bodies bodiesA = bodies.copy();
        double timeStepA = orbitalPeriod / stepsA;
        for (int i = 0; i < stepsA; i++) {
            solver.iterate(bodiesA, timeStepA);
        }
        double errorA = satellite.getDistance(bodiesA.getBody("Satellite"));

        Bodies bodiesB = bodies.copy();
        double timeStepB = orbitalPeriod / stepsB;
        for (int i = 0; i < stepsB; i++) {
            solver.iterate(bodiesB, timeStepB);
        }
        double errorB = satellite.getDistance(bodiesB.getBody("Satellite"));

        System.out.println("Satellite distance from the expected position after a full revolution with: ");
        System.out.println(" - " + stepsA + " steps: " + errorA);
        System.out.println(" - " + stepsB + " steps: " + errorB);
        System.out.println("Reducing the step size by factor of 2 reduces the error by factor of " + errorA / errorB);

    }
}
