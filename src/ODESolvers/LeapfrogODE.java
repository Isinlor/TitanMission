package ODESolvers;

import Simulation.*;
import Simulation.Bodies;
import Simulation.Force;
import Simulation.Vector;

import java.util.LinkedHashMap;

/**
 * From Wikipedia:
 * Leapfrog integration is a second-order method, in contrast to Euler integration, which is only first-order,
 * yet requires the same number of function evaluations per step. Unlike Euler integration, it is stable for oscillatory
 * motion , as long as the time-step t is constant, and t ≤ 2/ω.
 *
 * There are two primary strengths to leapfrog integration when applied to mechanics problems:
 *
 * - The first is the time-reversibility of the Leapfrog method. One can integrate forward n steps, and then reverse
 *   the direction of integration and integrate backwards n steps to arrive at the same starting position.
 * - The second strength is its symplectic nature, which implies that it conserves the (slightly modified) energy of
 *   dynamical systems. This is especially useful when computing orbital dynamics, as many other integration schemes,
 *   such as the (order-4) Runge–Kutta method, do not conserve energy and allow the system to drift substantially over
 *   time.
 *
 * https://en.wikipedia.org/wiki/Leapfrog_integration
 * https://rein.utsc.utoronto.ca/teaching/PSCB57_notes_lecture10.pdf
 */
public class LeapfrogODE implements ODESolver {

    public void iterate(Bodies bodies, double time) {

        for (Body body: bodies.getBodies()) {
            body.addPosition(body.getVelocity().product(time/2));
        }

        LinkedHashMap<String, Force> forces = bodies.getForces();
        for (Body body: bodies.getBodies()) {

            Force force = forces.get(body.getName());
            Vector acceleration = force.computeAcceleration(body.getMass());
            body.addVelocity(acceleration.product(time));
            body.addPosition(body.getVelocity().product(time/2));

        }

    }

    public String getName() {
        return "leapfrog";
    }

}
