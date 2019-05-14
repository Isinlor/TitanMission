package ODESolvers;

import EffectSystem.Effects;
import Simulation.*;

public class EulerODE extends AbstractODESolver implements ODESolver {
    public void iterate(Bodies bodies, double time) {

        Effects effects = getEffects(bodies, time);
        for (Body body: bodies.getBodies()) {

            Force force = new Force(effects.getEffect(body).getForce());
            Vector acceleration = force.computeAcceleration(body.getMass());
            body.addVelocity(acceleration.product(time));
            body.addPosition(body.getVelocity().product(time));

            if(body instanceof  RotatingBody) {
                RotatingBody rotatingBody = (RotatingBody) body;
                Vector torque = effects.getEffect(body).getTorque();
                Vector angularAcceleration = torque.quotient(rotatingBody.getMomentOfInertia());
                rotatingBody.addAngularVelocity(angularAcceleration.product(time));
                rotatingBody.addAngularDisplacement(rotatingBody.getAngularVelocity().product(time));
            }

        }

    }
    public String getName() {
        return "euler";
    }
}
