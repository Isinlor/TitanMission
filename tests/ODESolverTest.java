public class ODESolverTest extends SimpleUnitTest {
    public static void main(String[] args) {

        // https://math.stackexchange.com/questions/2873291/what-is-the-intuitive-meaning-of-order-of-accuracy-and-order-of-approximation
        it("results in 1/2^n reduction in error according to O(h^n) order of method", () -> {

            // https://en.wikipedia.org/wiki/Earth_radius
            // https://en.wikipedia.org/wiki/Earth_mass
            Body earth = new Body("Earth", new Vector(), new Vector(), 5.9722e24);
            Body object = new Body("Object", new Vector(6378100, 0,0), new Vector(), 1);
            Bodies bodies = new Bodies();
            bodies.addBody(earth);
            bodies.addBody(object);

            // https://en.wikipedia.org/wiki/Equations_for_a_falling_body
            double g = earth.computeAttraction(object).getLength();
            double d = 10; // ground truth
            double t = Math.sqrt(2 * d / g);

            object.addPosition(new Vector(d, 0,0));

            Vector startingPosition = object.getPosition();

            ODESolver solver = new MidpointODE();

            Bodies bodiesA = bodies.copy();
            Bodies bodiesB = bodies.copy();

            solver.iterate(bodiesA, t);
            solver.iterate(bodiesB, t/2);
            solver.iterate(bodiesB, t/2);

            double dA = bodiesA.getBody("Object").getPosition().euclideanDistance(startingPosition);
            double dB = bodiesB.getBody("Object").getPosition().euclideanDistance(startingPosition);

            double reductionInError = Math.abs(d - dB) / Math.abs(d - dA);

            assertTrue(
                Math.abs(reductionInError - 1./4.) < 0.1,
                "With reducing step size by 2 a second order method should reduce error by 4"
            );

        });

    }
}
