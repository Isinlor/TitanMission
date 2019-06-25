package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;

public class GravitationalEffectSystem implements EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        for(Body bodyA: bodies.getBodies()) {
            Force force = new Force();
            for (Body bodyB: bodies.getBodies()) {
                if(bodyA == bodyB) continue; // a body does not attract itself
                if(bodyB instanceof Spacecraft) continue; // spacecraft do not affect planets
                if(bodyB.getMass() < 2) continue; // negligible
                force = force.sum(bodyA.computeAttraction(bodyB));
            }
            effects.addEffect(bodyA, new Effect(force, new Vector()));
        }
    }
}
