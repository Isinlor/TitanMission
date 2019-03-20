/**
 * Data structure representing a topple.
 *
 * Allows to return two outputs from a function.
 */
class Tuple<X, Y> {

    private X x;
    private Y y;

    Tuple(X x, Y y) {
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
