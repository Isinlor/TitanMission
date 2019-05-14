package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;

public class CollisionEffectSystem implements EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        for(Body bodyA: bodies.getBodies()) {
            for (Body bodyB: bodies.getBodies()) {
                if(bodyA == bodyB) continue; // a body does not attract itself
                if(bodyA.computeDistance(bodyB) < bodyA.getRadius() + bodyB.getRadius()) {
                    Body crashedBody = bodyA.getMass() < bodyB.getMass() ? bodyA : bodyB;
                    bodies.removeBody(crashedBody);
                }
            }
        }
    }
}
