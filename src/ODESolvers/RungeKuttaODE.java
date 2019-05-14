package ODESolvers;

import java.util.LinkedHashMap;

import EffectSystem.Effects;
import Simulation.*;

public class RungeKuttaODE extends AbstractODESolver implements ODESolver {
    public void iterate(Bodies bodies, double time) {

        ODESolver eulerSolver = new EulerODE();

        Effects k1Effects = getEffects(bodies, time);

        Bodies k2Bodies = bodies.copy();
        eulerSolver.iterate(k2Bodies, time/2);
        Effects k2Effects = getEffects(k2Bodies, time);

        Bodies k3Bodies = k2Bodies.copy();
        eulerSolver.iterate(k3Bodies, time/2);
        Effects k3Effects = getEffects(k3Bodies, time);

        Bodies k4Bodies = k3Bodies.copy();
        eulerSolver.iterate(k4Bodies, time);
        Effects k4Effects = getEffects(k4Bodies, time);

        for (Body body: bodies.getBodies()) {

            Force k1Force = new Force(k1Effects.getEffect(body).getForce());
            Force k2Force = new Force(k2Effects.getEffect(body).getForce());
            Force k3Force = new Force(k3Effects.getEffect(body).getForce());
            Force k4Force = new Force(k4Effects.getEffect(body).getForce());

            Vector k1Acceleration = k1Force.computeAcceleration(body.getMass());
            Vector k2Acceleration = k2Force.computeAcceleration(body.getMass());
            Vector k3Acceleration = k3Force.computeAcceleration(body.getMass());
            Vector k4Acceleration = k4Force.computeAcceleration(body.getMass());

            Vector changeInVelocity = k1Acceleration
                .sum(k2Acceleration.product(2))
                .sum(k3Acceleration.product(2))
                .sum(k4Acceleration)
                .product(time/6);

            body.addVelocity(changeInVelocity);
            Vector changeInPosition = body.getVelocity().product(time);

            body.addPosition(changeInPosition);

        }

    }
    public String getName() {
        return "runge-kutta";
    }
}
