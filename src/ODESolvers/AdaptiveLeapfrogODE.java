package ODESolvers;

import Simulation.Bodies;
import Simulation.Body;

public class AdaptiveLeapfrogODE implements ODESolver {

    LeapfrogODE leapfrogODE = new LeapfrogODE();

    public void iterate(Bodies bodies, double time) {
        boolean adapt = false;
        for (Body bodyA: bodies.getBodies()) {
            if(bodyA.getMass() < 2) continue;
            for (Body bodyB: bodies.getBodies()) {
                if(bodyA == bodyB) continue;
                double distance = bodyA.getSurfaceToSurfaceDistance(bodyB);
                double approachSpeed = Math.abs(bodyA.getApproachSpeed(bodyB));
                if(approachSpeed * time > distance || distance < 100000) {
                    adapt = true;
                }
            }
        }

        if(adapt) {
            double split = 500;
            for (int i = 0; i < split; i++) {
                leapfrogODE.iterate(bodies, time / split);
            }
        } else {
            leapfrogODE.iterate(bodies, time);
        }

    }

    public String getName() {
        return "adaptive-leapfrog";
    }

}
