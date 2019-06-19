package Simulation;

import EventSystem.Event;
import EventSystem.EventDispatcher;
import EventSystem.EventListener;
import Utilities.FileSystem;

import java.nio.file.Paths;
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

    public Bodies() {
        this.bodies = new LinkedHashMap<>();
    }

    public Bodies(Collection<Body> bodies) {
        this();
        for(Body body: bodies) {
            addBody(body);
        }
    }

    public void advanceTime(double timeStep) {
        time += timeStep;
    }

    /**
     * Adds a body to the set.
     *
     * The body must have a unique name.
     */
    public void addBody(Body body) {
        if(bodies.containsValue(body)) throw new RuntimeException("Simulation [" + body.getName() + "] already added!");
        if(bodies.containsKey(body.getName())) throw new RuntimeException("Duplicated name [" + body.getName() + "]!");
        if(body instanceof BodiesAware) ((BodiesAware)body).setBodies(this);
        bodies.put(body.getName(), body);
    }

    /**
     * Add all given bodies.
     */
    public void addBodies(Bodies bodies) {
        for (Body body: bodies.getBodies()) {
            addBody(body.copy());
        }
    }

    /**
     * Remove body from the list of bodies.
     */
    public void removeBody(Body body) {
        bodies.remove(body.getName());
    }

    public void removeBody(String name) {
        bodies.remove(name);
    }

    /**
     * Returns body by a unique name.
     */
    public Body getBody(String name) {
        Body body = bodies.get(name);
        if(body == null) throw new RuntimeException("Body with name " + name + " not found!");
        return body;
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

    public double getBodiesCount() {
        return bodies.size();
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
     * Allows to apply arbitrary operation on all bodies.
     */
    public void apply(Consumer<Body> fn) {
        for(Body body: getBodies()) {
            fn.accept(body);
        }
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
        return copy;
    }

    public void save(String location, String filename) {
        try {
            FileSystem.write(Paths.get(location, filename), serialize());
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
