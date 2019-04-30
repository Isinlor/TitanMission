package Simulation;

import EventSystem.Event;
import EventSystem.EventDispatcher;
import EventSystem.EventListener;
import Utilities.FileSystem;

import java.util.*;
import java.util.function.Consumer;

/**
 * Represents set of bodies that interact with each other.
 *
 * Each body can be identified by unique name.
 */
public class Bodies {

    /**
     * Notice! Linked version of HashMap is used purposefully!
     * Order of floating point operations matters. (a+b != b+c)
     * Random order removes determinism that an optimization may relay on.
     * Linked version of HashMap assures consistent order.
     */
    private LinkedHashMap<String, Body> bodies;

    private double time;

    private EventDispatcher eventDispatcher = EventDispatcher.create();

    public Bodies() {
        this.bodies = new LinkedHashMap<>();
    }

    public Bodies(Collection<Body> bodies) {
        this();
        for(Body body: bodies) {
            addBody(body);
        }
    }

    /**
     * Adds a body to the set.
     *
     * The body must have a unique name.
     */
    public void addBody(Body body) {
        if(bodies.containsValue(body)) throw new RuntimeException("Simulation [" + body.getName() + "] already added!");
        if(bodies.containsKey(body.getName())) throw new RuntimeException("Duplicated name [" + body.getName() + "]!");
        bodies.put(body.getName(), body);
    }

    /**
     * Add all given bodies.
     */
    public void addBodies(Bodies bodies) {
        bodies.apply(this::addBody);
    }

    /**
     * Remove body from the list of bodies.
     */
    public void removeBody(Body body) {
        bodies.remove(body.getName());
    }

    /**
     * Returns body by a unique name.
     */
    public Body getBody(String name) {
        return bodies.get(name);
    }

    /**
     * Check whether a specific body is in the list.
     */
    public boolean hasBody(String name) {
        return bodies.containsKey(name);
    }

    /**
     * Returns body with the heaviest mass.
     */
    public Body getHeaviestBody() {
        Body heaviestBody = getBodies().iterator().next();
        for (Body body: getBodies()) {
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
    public Set<Body> getBodies() {
        return new LinkedHashSet<>(bodies.values());
    }

    /**
     * Total time of simulation.
     */
    public double getTime() {
        return time;
    }

    /**
     * Computes next time step in simulation.
     *
     * A force working on each body is computed and applied for specified time.
     */
    public void iterate(double time) {
        for (Body body: getBodies()) {
            body.applyForce(computeForce(body), time);
        }
        this.time += time;
    }

    /**
     * Allows to apply arbitrary operation on all bodies.
     */
    public void apply(Consumer<Body> fn) {
        for(Body body: getBodies()) {
            fn.accept(body);
        }
    }

    public void resetAll() {
        apply(Body::reset);
    }

    /**
     * Computes sum of forces that other bodies are working on body A.
     */
    public Force computeForce(Body bodyA) {
        Force force = new Force();
        for (Body bodyB: getBodies()) {
            if(bodyA == bodyB) continue;
            if(bodyB.getMass() < 2) continue; // negligible
            if(bodyA.computeDistance(bodyB) < bodyA.getRadius() + bodyB.getRadius()) {
                Body crashedBody = bodyA.getMass() < bodyB.getMass() ? bodyA : bodyB;
                removeBody(crashedBody);
                eventDispatcher.dispatch(new BodyCrashedEvent(crashedBody));
                if(crashedBody == bodyA) return new Force();
                continue;
            }
            force = force.sum(bodyA.computeAttraction(bodyB));
        }
        return force;
    }

    public LinkedHashMap<String, Force> getForces() {
        LinkedHashMap<String, Force> forces = new LinkedHashMap<>();
        for(Body body: this.getBodies()) {
            forces.put(body.getName(), computeForce(body));
        }
        return forces;
    }

    public void addEventListener(String eventName, EventListener<Event> listener) {
        eventDispatcher.addListener(eventName, listener);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Body body: getBodies()) {
            stringBuilder.append(body).append("\n");
        }
        return stringBuilder.toString();
    }

    public Bodies copy() {
        Bodies copy = new Bodies();
        for (Body body: getBodies()) {
            copy.addBody(body.copy());
        }
        copy.time = time;
        copy.eventDispatcher = eventDispatcher;
        return copy;
    }

    public void save(String location) {
        try {
            FileSystem.write(location, serialize());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize bodies to location " + location, e);
        }
    }

    public static Bodies load(String location) {
        try {
            return unserialize(FileSystem.read(location));
        } catch (Exception e) {
            throw new RuntimeException("Failed to unserialize bodies from location " + location, e);
        }
    }

    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Body body: getBodies()) {
            stringBuilder.append(body.serialize()).append("\n");
        }
        return stringBuilder.toString();
    }

    public static Bodies unserialize(String string) {
        Bodies bodies = new Bodies();
        String[] serializedBodies = string.trim().split("(\\r\\n|\\r|\\n)");
        for(String serializedBody: serializedBodies) {
            bodies.addBody(Body.unserialize(serializedBody));
        }
        return bodies;
    }

}
