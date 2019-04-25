public class VectorTest extends SimpleUnitTest {

    public static void main(String[] args) {

        it("allows to make a null vector", () -> {
            Vector vector = new Vector();
            assertTrue(vector.x == 0);
            assertTrue(vector.y == 0);
            assertTrue(vector.z == 0);
        });

    }

}
