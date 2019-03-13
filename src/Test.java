import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class Test {

    /**
     * Some simple test.
     */
    public static void main(String[] args) {

        Set<Body> setOfBodies = new HashSet<Body>();
        setOfBodies.add(new Body(new Vector(100, 100), new Vector(0, 0), 2e13));
        setOfBodies.add(new Body(new Vector(30, 30), new Vector(2.3, -1.5), 1));
        setOfBodies.add(new Body(new Vector(100, 150), new Vector(-5, 0), 1e10));

        Bodies bodies = new Bodies(setOfBodies);

        System.out.println(bodies);

        for (int i = 0; i < 100; i++) {
            bodies.iterate(1);
        }

        System.out.println(bodies);

    }

}
