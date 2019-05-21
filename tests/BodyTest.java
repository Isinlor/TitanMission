import Simulation.Body;
import Simulation.Vector;

public class BodyTest extends SimpleUnitTest {
    public static void main(String[] args) {

        // see: https://en.wikipedia.org/wiki/Relative_velocity
        it("computes simple relative velocities", () -> {

            Body a = new Body(
                "A",
                new Vector(),
                new Vector(),
                1
            );

            Body b = new Body(
                "A",
                new Vector(100, 0),
                new Vector(100, 0),
                1
            );

            assertEqual(a.getRelativeVelocity(b).x, 100.0, 1, "Relative speed as seen from A.");
            assertEqual(b.getRelativeVelocity(a).x, -100.0, 1, "Relative speed as seen from B.");

            assertEqual(a.getApproachSpeed(b), -100.0, 1, "Approach speed of A to B.");
            assertEqual(b.getApproachSpeed(a), -100.0, 1, "Approach speed of B to A.");

        });

    }
}
