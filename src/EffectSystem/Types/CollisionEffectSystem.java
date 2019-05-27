package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;
import Utilities.Units;
import Utilities.Utils;
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
                    String details = "";

                    if(crashedBody.getMeta().has("x")) {
                        details += "\tAway from target x : " + Units.distance(Math.abs(crashedBody.getPosition().x - Double.parseDouble(crashedBody.getMeta().get("x"))));
                        details += " y : " + Units.distance(Math.abs(crashedBody.getPosition().y - Double.parseDouble(crashedBody.getMeta().get("y"))));
                    }

                    details +=
                        "\tApproach speed: " + Units.speed(crashedBody.getApproachSpeed(crashedIntoBody)) +
                        "\tTime: " + Units.time(bodies.getTime());
                    if(crashedBody instanceof RotatingBody) {
                        RotatingBody crashedRotatingBody = (RotatingBody)crashedBody;

                        Vector relativePosition = crashedIntoBody.getRelativePosition(crashedBody);

                        double verticalAngle = Utils.clockAngle(
                            relativePosition.x,
                            -relativePosition.y // FIXME: y-axis reversed (swing)
                        );

                        double crashingBodyAngle = crashedRotatingBody.getAngularDisplacement().z;

                        double angle = Utils.getSignedDistanceBetweenAngles(verticalAngle, crashingBodyAngle);

                        details += "\tAngle: " + Math.round(Math.toDegrees(angle) * 1000) / 1000.;
                    }

                    Simulation.logger.log(crashedBody.getName() + " crashed into " + crashedIntoBody.getName() + "! \t" + details);
                    bodies.removeBody(crashedBody);
                }

            }
        }
    }
}
