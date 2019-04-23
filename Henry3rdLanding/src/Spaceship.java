public class Spaceship extends Body {


    public Spaceship(String name, Vector position, Vector velocity, double mass){
        super(name, position, velocity, mass);
    }

    public void iterate(Body target, Force slowingDownForce, double time) {
        applyForce(computeAttraction(target).sum(slowingDownForce),time);
    }

    public void iterate(Body target, double time) {
        applyForce(computeAttraction(target), time);
    }

    private Force computeForce(Body bodyA){
        Force force = this.computeAttraction(bodyA);
        return force;
    }

}
