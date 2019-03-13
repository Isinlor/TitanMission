import java.math.BigDecimal;
import java.math.MathContext;

class Vector {

    double x;
    double y;

    Vector() {}

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    double getLength() {
        return Math.sqrt(
                Math.pow(x, 2.0) +
                Math.pow(y, 2.0)
        );
    }

    Vector product(double scalar) {
        return new Vector(
                x * scalar,
                y * scalar
        );
    }

    Vector sum(Vector vector) {
        return new Vector(
                x + vector.x,
                y + vector.y
        );
    }

    Vector difference(Vector vector) {
        return new Vector(
                x - vector.x,
                y - vector.y
        );
    }

    Vector unitVector() {
        double length = getLength();
        return new Vector(
            x / length,
            y / length
        );
    }

    double euclideanDistance(Vector vector) {
        return Math.sqrt(
                Math.pow(x - vector.x, 2.0) +
                Math.pow(y - vector.y, 2.0)
        );
    }

    public String toString() {
        return "(x:" + Utils.round(x) + " y:" + Utils.round(y) + ")";
    }

}
