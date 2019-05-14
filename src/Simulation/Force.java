package Simulation;

/**
 * Representation of a force based on a vector.
 */
public class Force extends Vector {

    public Force() {
    }

    public Force (Vector vector) {
        this(vector.x, vector.y, vector.z);
    }

    public Force(double x, double y) {
        super(x, y);
    }

    public Force(double x, double y, double z) {
        super(x, y, z);
    }

    public Force sum(Force force) {
        return new Force(super.sum(force));
    }

    /**
     * Newton's second law says F = m*a, so a = F/m
     * @link https://en.wikipedia.org/wiki/Newton's_laws_of_motion#Newton's_second_law
     */
    public Vector computeAcceleration(double mass) {
        return new Vector(
          x / mass,
          y / mass,
          z / mass
        );
    }

}
