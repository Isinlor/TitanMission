package Simulation;

/**
 * This interface allows implementing bodies to maintain relationships between bodies.
 * It solves an issue where due to copy a reference to a related body is lost.
 * Using bodies names as references and queering them from set bodies solves that issue.
 *
 * @see Bodies#addBody(Simulation.Body)
 */
public interface BodiesAware {
    public void setBodies(Bodies bodies);
}
