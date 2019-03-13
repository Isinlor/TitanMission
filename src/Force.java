class Force extends Vector {

    Force() {
    }

    Force (Vector vector) {
        this(vector.x, vector.y);
    }

    Force(double x, double y) {
        super(x, y);
    }

    Force sum(Force force) {
        return new Force(super.sum(force));
    }

    Vector computeAcceleration(double mass) {
        return new Vector(
          x / mass,
          y / mass
        );
    }

}
