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
                z + vector.z
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

    /**
     * x' = x
     * y' = y*cos q - z*sin q
     * z' = y*sin q + z*cos q
     *
     * https://www.cs.helsinki.fi/group/goa/mallinnus/3dtransf/3drot.html
     */
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

    /**
     * x' = z*sin q + x*cos q
     * y' = y
     * z' = z*cos q - x*sin q
     *
     * https://www.cs.helsinki.fi/group/goa/mallinnus/3dtransf/3drot.html
     */
    Vector rotateAroundAxisY(Vector center, double theta) {
        double cosTheta = Math.cos(theta);
        double sinTheta = Math.sin(theta);
        Vector centered = difference(center);
        Vector rotated = new Vector(
            centered.z * sinTheta - centered.x * cosTheta,
            centered.y,
            centered.z * cosTheta + centered.x * sinTheta
        );
        return rotated.sum(center);
    }

    /**
     * x' = x*cos q - y*sin q
     * y' = x*sin q + y*cos q
     * z' = z
     *
     * https://www.cs.helsinki.fi/group/goa/mallinnus/3dtransf/3drot.html
     */
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
