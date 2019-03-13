import java.util.HashSet;
import java.util.Set;

class Bodies {

    private Set<Body> bodies;

    public Bodies(Set<Body> bodies) {
        this.bodies = bodies;
    }

    public Set<Body> getBodies() {
        return bodies;
    }

    void iterate(double time) {
        for (Body body: bodies) {
            body.applyForce(computeForce(body), time);
        }
    }

    private Force computeForce(Body bodyA) {
        Force force = new Force();
        for (Body bodyB: bodies) {
            if(bodyA == bodyB) continue;
            force = force.sum(bodyA.computeAttraction(bodyB));
        }
        return force;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Body body: bodies) {
            stringBuilder.append(body).append("\n");
        }
        return stringBuilder.toString();
    }

}
