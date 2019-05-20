package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;
import Visualisation.Simulation;

public class CollisionEffectSystem implements EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        Body[] bodiesArray = bodies.getBodies().toArray(new Body[0]);
        for (int i = 0; i < bodiesArray.length; i++) {
            Body bodyA = bodiesArray[i];
            for (int j = i + 1; j < bodiesArray.length; j++) {
                Body bodyB = bodiesArray[j];

                if(bodyA.computeDistance(bodyB) < bodyA.getRadius() + bodyB.getRadius()) {
                    Body crashedBody = bodyA.getMass() < bodyB.getMass() ? bodyA : bodyB;
                    Simulation.logger.log("Body crashed! " + crashedBody.toString());
                    bodies.removeBody(crashedBody);
                }

            }
        }
    }
}
