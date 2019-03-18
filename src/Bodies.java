import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Represents set of bodies that interact with each other.
 *
 * Each body can be identified by unique name.
 */
class Bodies<M extends BodyMeta> {

    private Map<String, Body<M>> bodies;

    Bodies() {
        this.bodies = new HashMap<>();
    }

    Bodies(Map<String, Body<M>> bodies) {
        this.bodies = bodies;
    }

    /**
     * Adds a body to the set.
     *
     * The body must have a unique name.
     */
    void addBody(Body<M> body) {
        if(bodies.containsValue(body)) throw new RuntimeException("Body [" + body.getName() + "] already added!");
        if(bodies.containsKey(body.getName())) throw new RuntimeException("Duplicated name [" + body.getName() + "]!");
        bodies.put(body.getName(), body);
    }

    /**
     * Returns body by a unique name.
     */
    Body<M> getBody(String name) {
        return bodies.get(name);
    }

    /**
     * Returns set of bodies.
     */
    Set<Body<M>> getBodies() {
        return new HashSet<Body<M>>(bodies.values());
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
     * Allows to apply arbitrary operation on all bodies.
     */
    void apply(Consumer<Body<M>> fn) {
        for(Body<M> body: getBodies()) {
            fn.accept(body);
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

    Bodies<M> copy() {
        return new Bodies<M>(getCopyBodies());
    }

    private HashMap<String, Body<M>> getCopyBodies() {
        HashMap<String, Body<M>> setOfBodies = new HashMap<>();
        for (Body<M> body: getBodies()) {
            setOfBodies.put(body.getName(), body.copy());
        }
        return setOfBodies;
    }

}
