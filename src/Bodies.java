import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents set of bodies that interact with each other.
 */
class Bodies {

    private HashMap<String, Body> bodies;

    Bodies(HashMap<String, Body> bodies) {
        this.bodies = bodies;
    }

    Body getBody(String string) {
        return bodies.get(string);
    }

    Set<Map.Entry<String, Body>> getEntries() {
        return bodies.entrySet();
    }

    Set<Body> getBodies() {
        return new HashSet<>(bodies.values());
    }

    Vectors getPositions() {
        return new Vectors(
            getBodies().stream().map(Body::getPosition).collect(Collectors.toSet())
        );
    }

    /**
     * Computes next time step in simulation.
     *
     * A force working on each body is computed and applied for specified time.
     */
    void iterate(double time) {
        for (Body body: getBodies()) {
            body.applyForce(computeForce(body), time);
        }
    }

    /**
     * Computes sum of forces that other bodies are working on body A.
     */
    private Force computeForce(Body bodyA) {
        Force force = new Force();
        for (Body bodyB: getBodies()) {
            if(bodyA == bodyB) continue;
            force = force.sum(bodyA.computeAttraction(bodyB));
        }
        return force;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Body body: getBodies()) {
            stringBuilder.append(body).append("\n");
        }
        return stringBuilder.toString();
    }

    Bodies copy() {
        HashMap<String, Body> setOfBodies = getCopyBodies();
        return new Bodies(setOfBodies);
    }

    protected HashMap<String, Body> getCopyBodies() {
        HashMap<String, Body> setOfBodies = new HashMap<>();
        for (Map.Entry<String, Body> body: bodies.entrySet()) {
            setOfBodies.put(body.getKey(), body.getValue().copy());
        }
        return setOfBodies;
    }

}
