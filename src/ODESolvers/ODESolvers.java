package ODESolvers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Static repository of ODE solvers.
 */
public class ODESolvers {

    final static private Map<String, ODESolver> solvers = new HashMap<>();

    static {
        solvers.put(new EulerODE().getName(), new EulerODE());
        solvers.put(new MidpointODE().getName(), new MidpointODE());
        solvers.put(new LeapfrogODE().getName(), new LeapfrogODE());
        solvers.put(new RungeKuttaODE().getName(), new RungeKuttaODE());
    }

    public static ODESolver getODESolver(String name) {
        return solvers.get(name);
    }

    public static Collection<ODESolver> getODESolvers() {
        return solvers.values();
    }

}
