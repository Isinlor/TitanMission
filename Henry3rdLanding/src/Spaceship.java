public class Spaceship extends Body {


    public Spaceship(String name, Vector position, Vector velocity, double mass){
        super(name, position, velocity, mass);
    }

    public void iterate(Body target, Force slowingDown, Double time) {
        applyForce(computeAttraction(target).sum(slowingDown),time);
    }

    private Force computeForce(Body bodyA){
        Force force = this.computeAttraction(bodyA);
        return force;
    }

}
