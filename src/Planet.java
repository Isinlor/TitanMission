class Planet extends Body {
    private double radius;

    Planet(String name, double mass, double radius, Vector position, Vector velocity) {
        super(name, position, velocity, mass);
        this.radius = radius;
    }

    double getRadius() {
        return radius;
    }

}
