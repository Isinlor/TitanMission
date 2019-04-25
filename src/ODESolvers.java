import java.util.HashMap;
import java.util.Map;

/**
 * Static repository of ODE solvers.
 */
class ODESolvers {

    final static private Map<String, ODESolver> solvers = new HashMap<>();

    static {
        solvers.put(new EulerODE().getName(), new EulerODE());
        solvers.put(new MidpointODE().getName(), new MidpointODE());
        solvers.put(new RungaKuttaODE().getName(), new RungaKuttaODE());
    }

    static ODESolver getODESolver(String name) {
        return solvers.get(name);
    }

}
