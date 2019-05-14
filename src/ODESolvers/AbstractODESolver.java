package ODESolvers;

import EffectSystem.*;
import EffectSystem.Types.CollisionEffectSystem;
import EffectSystem.Types.CompositeEffectSystem;
import EffectSystem.Types.ControllerEffectSystem;
import EffectSystem.Types.GravitationalEffectSystem;
import Simulation.Bodies;

public class AbstractODESolver {

    static protected EffectSystem effectSystem = new CompositeEffectSystem(
        new GravitationalEffectSystem(),
        new ControllerEffectSystem(),
        new CollisionEffectSystem()
    );

    protected Effects getEffects(Bodies bodies, double timeStep) {
        Effects effects = new Effects(bodies);
        effectSystem.collectEffects(bodies, effects, timeStep);
        return effects;
    }

}
