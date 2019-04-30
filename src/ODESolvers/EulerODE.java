package ODESolvers;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class EulerODE implements ODESolver {
    public void iterate(Bodies bodies, double time) {
        bodies.iterate(time);
    }
    public String getName() {
        return "euler";
    }
}