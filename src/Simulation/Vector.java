package Simulation;

import Utilities.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Vector with different operators. Allows to hide dimensionality.
 */
public class Vector {

    public double x;
    public double y;
    public double z;

    public Vector() {}

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        if(!isRealValued()) throw new RuntimeException("The vector is not real valued: " + this);
    }

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        if(!isRealValued()) throw new RuntimeException("The vector is not real valued: " + this);
    }

    public boolean isEqualTo(Vector vector, double tolerance) {
        Vector absDiff = this.difference(vector).abs();
        return absDiff.x <= tolerance
            && absDiff.y <= tolerance
            && absDiff.z <= tolerance;
    }

    public double getLength() {
        return Math.sqrt(
                Math.pow(x, 2.0) +
                Math.pow(y, 2.0) +
                Math.pow(z, 2.0)
        );
    }

    public Vector quotient(double scalar) {
        return new Vector(
            x / scalar,
            y / scalar,
            z / scalar
        );
    }

    public Vector product(double scalar) {
        return new Vector(
                x * scalar,
                y * scalar,
                z * scalar
        );
    }

    public double dotProduct(Vector vector) {
        return x * vector.x + y * vector.y + z * vector.z;
    }

    public Vector crossProduct(Vector vector) {

        return new Vector(
            y*vector.z - z*vector.y,
            z*vector.x - x*vector.z,
            x*vector.y - y*vector.x
        );

    }

    public Vector sum(Vector vector) {
        return new Vector(
                x + vector.x,
                y + vector.y,
                z + vector.z
        );
    }

    public Vector difference(Vector vector) {
        return new Vector(
                x - vector.x,
                y - vector.y,
                z - vector.z
        );
    }

    public Vector unitVector() {
        double length = getLength();
        if(length == 0) return new Vector();
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
    public Vector rotateAroundAxisX(Vector center, double theta) {
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
    public Vector rotateAroundAxisY(Vector center, double theta) {
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
    public Vector rotateAroundAxisZ(Vector center, double theta) {
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

    public double euclideanDistance(Vector vector) {
        return Math.sqrt(
                Math.pow(x - vector.x, 2.0) +
                Math.pow(y - vector.y, 2.0) +
                Math.pow(z - vector.z, 2.0)
        );
    }

    public Vector abs() {
        return new Vector(
          Math.abs(x),
          Math.abs(y),
          Math.abs(z)
        );
    }

    public Vector mod(double scalar) {
        return new Vector(
          x % scalar,
          y % scalar,
          z % scalar
        );
    }

    public boolean isRealValued() {
        return Utils.isRealNumber(x) && Utils.isRealNumber(y) && Utils.isRealNumber(z);
    }

    public String toString() {
        return "(x:" + Utils.round(x) + " y:" + Utils.round(y) + " z:" + Utils.round(z) + ")";
    }

    /**
     * Serialize vector so that it can be saved.
     */
    public String serialize() {
        return x + ", " + y + ", " + z;
    }

    /**
     * Unserialize saved vector.
     */
    public static Vector unserialize(String string) {

        Pattern pattern = Pattern.compile("" +
            "(?<x>[^,]+),\\s+(?<y>[^,]+),\\s+(?<z>[^,]+)"
        );
        Matcher matcher = pattern.matcher(string);
        matcher.matches();

        return new Vector(
            Double.parseDouble(matcher.group("x")),
            Double.parseDouble(matcher.group("y")),
            Double.parseDouble(matcher.group("z"))
        );
    }

}
