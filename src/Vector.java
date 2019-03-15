import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Vector with different operators. Allows to hide dimensionality.
 */
class Vector {

    double x;
    double y;
    double z;

    Vector() {}

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double getLength() {
        return Math.sqrt(
                Math.pow(x, 2.0) +
                Math.pow(y, 2.0) +
                Math.pow(z, 2.0)
        );
    }

    Vector product(double scalar) {
        return new Vector(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    Vector sum(Vector vector) {
        return new Vector(
                x + vector.x,
                y + vector.y,
                z * vector.z
        );
    }

    Vector difference(Vector vector) {
        return new Vector(
                x - vector.x,
                y - vector.y,
                z - vector.z
        );
    }

    Vector unitVector() {
        double length = getLength();
        return new Vector(
            x / length,
            y / length,
            z / length
        );
    }

    Vector rotateAroundAxisX(Vector center, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Vector centered = difference(center);
        Vector rotated = new Vector(
            centered.x,
            centered.y * cosTheta - centered.z * sinTheta,
            centered.y * sinTheta + centered.z * cosTheta
        );
        return rotated.sum(center);
    }

    Vector rotateAroundAxisY(Vector center, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Vector centered = difference(center);
        Vector rotated = new Vector(
            centered.x * cosTheta - centered.z * sinTheta,
            centered.y,
            - centered.x * sinTheta + centered.z * cosTheta
        );
        return rotated.sum(center);
    }

    Vector rotateAroundAxisZ(Vector center, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Vector centered = difference(center);
        Vector rotated = new Vector(
            centered.x * cosTheta - centered.y * sinTheta,
            centered.x * sinTheta + centered.y * cosTheta,
            centered.z
        );
        return rotated.sum(center);
    }

    double euclideanDistance(Vector vector) {
        return Math.sqrt(
                Math.pow(x - vector.x, 2.0) +
                Math.pow(y - vector.y, 2.0) +
                Math.pow(z - vector.z, 2.0)
        );
    }

    public String toString() {
        return "(x:" + Utils.round(x) + " y:" + Utils.round(y) + " z:" + Utils.round(z) + ")";
    }

}
