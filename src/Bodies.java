import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Represents set of bodies that interact with each other.
 *
 * Each body can be identified by unique name.
 */
class Bodies<M extends BodyMeta> {

    /**
     * Notice! Linked version of HashMap is used purposefully!
     * Order of floating point operations matters. (a+b != b+c)
     * Random order removes determinism that an optimization may relay on.
     * Linked version of HashMap assures consistent order.
     */
    private LinkedHashMap<String, Body<M>> bodies;

    private double time;

    Bodies() {
        this.bodies = new LinkedHashMap<>();
    }

    Bodies(Collection<Body<M>> bodies) {
        this();
        for(Body<M> body: bodies) {
            addBody(body);
        }
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
     * Add all given bodies.
     */
    void addBodies(Bodies<M> bodies) {
        bodies.apply(this::addBody);
    }

    /**
     * Remove body from the list of bodies.
     */
    void removeBody(Body body) {
        bodies.remove(body.getName());
    }

    /**
     * Returns body by a unique name.
     */
    Body<M> getBody(String name) {
        return bodies.get(name);
    }

    /**
     * Returns body with the heaviest mass.
     */
    Body<M> getHeaviestBody() {
        Body<M> heaviestBody = getBodies().iterator().next();
        for (Body<M> body: getBodies()) {
            if(heaviestBody.getMass() < body.getMass()) heaviestBody = body;
        }
        return heaviestBody;
    }

    /**
     * Returns set of bodies.
     *
     * Notice! Linked version of HashSet is used purposefully.
     * @see Bodies documentation for more details.
     */
    Set<Body<M>> getBodies() {
        return new LinkedHashSet<>(bodies.values());
    }

    /**
     * Total time of simulation.
     */
    double getTime() {
        return time;
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
        this.time += time;
    }

    /**
     * Allows to apply arbitrary operation on all bodies.
     */
    void apply(Consumer<Body<M>> fn) {
        for(Body<M> body: getBodies()) {
            fn.accept(body);
        }
    }

    void resetAll() {
        apply(Body::reset);
    }

    /**
     * Computes sum of forces that other bodies are working on body A.
     */
    private Force computeForce(Body bodyA) {
        Force force = new Force();
        for (Body bodyB: getBodies()) {
            if(bodyA == bodyB) continue;
            if(bodyB.getMass() < 2) continue; // negligible
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
        Bodies<M> copy = new Bodies<M>();
        for (Body<M> body: getBodies()) {
            copy.addBody(body.copy());
        }
        copy.time = time;
        return copy;
    }

    void save(String location) {
        try {
            FileSystem.write(location, serialize());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize bodies to location " + location, e);
        }
    }

    static Bodies load(String location) {
        try {
            return unserialize(FileSystem.read(location));
        } catch (Exception e) {
            throw new RuntimeException("Failed to unserialize bodies from location " + location, e);
        }
    }

    String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Body body: getBodies()) {
            stringBuilder.append(body.serialize()).append("\n");
        }
        return stringBuilder.toString();
    }

    static Bodies unserialize(String string) {
        Bodies bodies = new Bodies();
        String[] serializedBodies = string.trim().split("(\\r\\n|\\r|\\n)");
        for(String serializedBody: serializedBodies) {
            bodies.addBody(Body.unserialize(serializedBody));
        }
        return bodies;
    }

}
