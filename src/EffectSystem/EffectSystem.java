package EffectSystem;

import Simulation.Bodies;

public interface EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep);
}