import java.util.HashMap;
import java.util.Map;

/**
 * Static repository of ODE solvers.
 */
public class ODESolvers {

    final static private Map<String, ODESolver> solvers = new HashMap<>();

    static {
        solvers.put("euler", new EulerODE());
        solvers.put("midpoint", new MidpointODE());
    }

    static ODESolver getODESolver(String name) {
        return solvers.get(name);
    }

}
