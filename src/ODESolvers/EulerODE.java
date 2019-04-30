package ODESolvers;

import Simulation.*;

public class EulerODE implements ODESolver {
    public void iterate(Bodies bodies, double time) {
        bodies.simulate(time);
    }
    public String getName() {
        return "euler";
    }
}
