import java.util.HashSet;
import java.util.Set;

/**
 * Represents set of bodies that interact with each other.
 */
class Bodies {

    private Set<Body> bodies;

    public Bodies(Set<Body> bodies) {
        this.bodies = bodies;
    }

    public Set<Body> getBodies() {
        return bodies;
    }

    /**
     * Computes next time step in simulation.
     *
     * A force working on each body is computed and applied for specified time.
     */
    void iterate(double time) {
        for (Body body: bodies) {
            body.applyForce(computeForce(body), time);
        }
    }

    /**
     * Computes sum of forces that other bodies are working on body A.
     */
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
