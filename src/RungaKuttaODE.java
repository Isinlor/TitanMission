import java.util.LinkedHashMap;

class RungaKuttaODE implements ODESolver {
    public void iterate(Bodies bodies, double time) {

        // yM = y0 + h*f(x0, y0)
        // y1 = y0 + 1/2*h(f(x0, y0) + f(x0 + h, yM))

        LinkedHashMap<String, Force> forces = bodies.getForces();

        Bodies k2Bodies = bodies.copy();
        k2Bodies.iterate(time/2);
        LinkedHashMap<String, Force> k2Forces = k2Bodies.getForces();

        Bodies k3Bodies = k2Bodies.copy();
        k3Bodies.iterate(time/2);
        LinkedHashMap<String, Force> k3Forces = k3Bodies.getForces();

        Bodies k4Bodies = k3Bodies.copy();
        k4Bodies.iterate(time);
        LinkedHashMap<String, Force> k4Forces = k4Bodies.getForces();

        for (Body body: bodies.getBodies()) {
            Force k1Force = forces.get(body.getName());
            Force k2Force = k2Forces.get(body.getName());
            Force k3Force = k3Forces.get(body.getName());
            Force k4Force = k4Forces.get(body.getName());


            Vector k1Acceleration = k1Force.computeAcceleration(body.getMass());
            // k2Bodies.getBody(body.getName()).addVelocity(k1Acceleration.product(time/2));
            Vector k2Acceleration = k2Force.computeAcceleration(body.getMass());
            // k3Bodies.getBody(body.getName()).addVelocity(k2Acceleration.product(time/2));
            Vector k3Acceleration = k3Force.computeAcceleration(body.getMass());
            // k4Bodies.getBody(body.getName()).addVelocity(k3Acceleration.product(time));
            Vector k4Acceleration = k4Force.computeAcceleration(body.getMass());

            Vector changeInVelocity = k1Acceleration.sum(k2Acceleration.product(2)).sum(k3Acceleration.product(2)).sum(k4Acceleration).product(time/6);

            body.addVelocity(changeInVelocity);
            Vector changeInPosition = body.getVelocity().product(time);

            body.addPosition(changeInPosition);
        }

    }
}
