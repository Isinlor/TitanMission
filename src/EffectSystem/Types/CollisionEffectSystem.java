package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;
import Utilities.Units;
import Visualisation.Simulation;

public class CollisionEffectSystem implements EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        Body[] bodiesArray = bodies.getBodies().toArray(new Body[0]);
        for (int i = 0; i < bodiesArray.length; i++) {
            Body bodyA = bodiesArray[i];
            for (int j = i + 1; j < bodiesArray.length; j++) {
                Body bodyB = bodiesArray[j];

                if(bodyA.getDistance(bodyB) < bodyA.getRadius() + bodyB.getRadius()) {
                    Body crashedBody = bodyA.getMass() < bodyB.getMass() ? bodyA : bodyB;
                    Body crashedIntoBody = bodyA.getMass() < bodyB.getMass() ? bodyB : bodyA;
                    String details = "Approach speed: " + Units.speed(crashedIntoBody.getApproachSpeed(crashedBody));
                    Simulation.logger.log(crashedBody.getName() + " crashed into " + crashedIntoBody.getName() + "! \t\t" + details);
                    bodies.removeBody(crashedBody);
                }

            }
        }
    }
}
