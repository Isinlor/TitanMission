package ODESolvers;

import java.util.LinkedHashMap;

import EffectSystem.Effects;
import Simulation.*;

public class MidpointODE extends AbstractODESolver implements ODESolver {
    public void iterate(Bodies bodies, double time) {

        // yM = y0 + h*f(x0, y0)
        // y1 = y0 + 1/2*h(f(x0, y0) + f(x0 + h, yM))

        ODESolver eulerSolver = new EulerODE();

        Bodies approximateBodies = bodies.copy();
        eulerSolver.iterate(approximateBodies, time);

        Effects startEffects = getEffects(bodies, time);
        Effects endEffects = getEffects(approximateBodies, time);
        for (Body body: bodies.getBodies()) {
            Force startForce = new Force(startEffects.getEffect(body).getForce());
            Force endForce = new Force(endEffects.getEffect(body).getForce());
            Vector endAcceleration = endForce.computeAcceleration(body.getMass());
            Vector acceleration = startForce.computeAcceleration(body.getMass());
            Vector changeInVelocity = acceleration.sum(endAcceleration).product(time/2);
            body.addVelocity(changeInVelocity);
            Vector changeInPosition = body.getVelocity().product(time);
            body.addPosition(changeInPosition);
        }

    }
    public String getName() {
        return "midpoint";
    }
}
