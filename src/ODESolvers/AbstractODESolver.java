package ODESolvers;

import EffectSystem.*;
import EffectSystem.Types.*;
import Simulation.Bodies;

public class AbstractODESolver {

    static protected EffectSystem effectSystem = new CompositeEffectSystem(
        new GravitationalEffectSystem(),
        new ControllerEffectSystem(),
        new CollisionEffectSystem(),
            new WindEffectSystem()
    );

    protected Effects getEffects(Bodies bodies, double timeStep) {
        Effects effects = new Effects(bodies);
        effectSystem.collectEffects(bodies, effects, timeStep);
        return effects;
    }

}
