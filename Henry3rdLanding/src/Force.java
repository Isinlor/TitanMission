/**
 * Representation of a force based on a vector.
 */
class Force extends Vector {

    Force() {
    }

    Force (Vector vector) {
        this(vector.x, vector.y, vector.z);
    }

    Force(double x, double y) {
        super(x, y);
    }

    Force(double x, double y, double z) {
        super(x, y, z);
    }

    Force sum(Force force) {
        return new Force(super.sum(force));
    }

    /**
     * Newton's second law says F = m*a, so a = F/m
     * @link https://en.wikipedia.org/wiki/Newton's_laws_of_motion#Newton's_second_law
     */
    Vector computeAcceleration(double mass) {
        return new Vector(
                x / mass,
                y / mass,
                z / mass
        );
    }

}
