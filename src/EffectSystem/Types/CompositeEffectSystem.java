package EffectSystem.Types;

import EffectSystem.*;
import Simulation.Bodies;

public class CompositeEffectSystem implements EffectSystem {

    private EffectSystem[] effectSystems;

    public CompositeEffectSystem(EffectSystem... effectSystems) {
        this.effectSystems = effectSystems;
    }

    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        for(EffectSystem effectsSystem: effectSystems) {
            effectsSystem.collectEffects(bodies, effects, timeStep);
        }
    }

}
