import java.util.LinkedHashMap;

class MidpointODE implements ODESolver {
    public void iterate(Bodies bodies, double time) {

        // yM = y0 + h*f(x0, y0)
        // y1 = y0 + 1/2*h(f(x0, y0) + f(x0 + h, yM))

        LinkedHashMap<String, Force> forces = bodies.getForces();
        Bodies approximateBodies = bodies.copy();
        approximateBodies.iterate(time);
        LinkedHashMap<String, Force> endForces = approximateBodies.getForces();
        for (Body body: bodies.getBodies()) {
            Force startForce = forces.get(body.getName());
            Force endForce = endForces.get(body.getName());
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
