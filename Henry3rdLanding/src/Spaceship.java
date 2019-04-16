public class Spaceship extends Body {

    Force slowingDown;
    Body target;

    public Spaceship(String name, Vector position, Vector velocity, double mass, Force slowingDown, Body target){
        super(name, position, velocity, mass);
        this.slowingDown=slowingDown;
        this.target=target;

    }

    void iterate(double time) {
        this.applyForce(this.computeAttraction(target).sum(slowingDown),time);
    }

    private Force computeForce(Body bodyA){
        Force force = this.computeAttraction(bodyA);
        return force;
    }

}
