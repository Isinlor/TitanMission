import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class VectorTest extends SimpleUnitTest {

    public static void main(String[] args) {

        it("allows to make a null vector", () -> {
            Vector vector = new Vector();
            assertTrue(vector.x == 0);
            assertTrue(vector.y == 0);
            assertTrue(vector.z == 0);
        });

        it("allows to compare vectors with tolerance", () -> {
            assertTrue(new Vector().isEqualTo(new Vector(), 0));
            assertTrue(new Vector(0.8, 0.8, 0.8).isEqualTo(new Vector(0.4, 0.4, 0.4), 0.4));
            assertTrue(new Vector(0.4, 0.4, 0.4).isEqualTo(new Vector(0.8, 0.8, 0.8), 0.4));
        });

        it("allows to compute cross product", () -> {
            Vector a = new Vector(2,3,4);
            Vector b = new Vector(5,6,7);

            Vector c = a.crossProduct(b);
            assertTrue(c.isEqualTo(new Vector(-3, 6, -3), 0));

            Vector d = b.crossProduct(a);
            assertTrue(c.isEqualTo(d.product(-1),0));
        });

    }

}
