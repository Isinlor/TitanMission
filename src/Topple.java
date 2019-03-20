/**
 * Data structure representing a topple.
 *
 * Allows to return two outputs from a function.
 */
public class Topple<X, Y> {

    private X x;
    private Y y;

    Topple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    X getX() {
        return x;
    }

    Y getY() {
        return y;
    }

}
