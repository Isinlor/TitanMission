package EffectSystem.Types;

import EffectSystem.*;
import Simulation.*;
import Simulation.Atmosphere.AtmosphereModel;
import Simulation.Atmosphere.NullAtmosphereModel;

/**
 * See: https://en.wikipedia.org/wiki/Drag_equation
 */
public class DragEffectSystem implements EffectSystem {

    /**
     * Drag coefficient multiplied with 1/2 from the drag equation.
     *
     * Assumes that bodies are spheres.
     * Assumes drag coefficient of 0.47 based on link below:
     * https://web.archive.org/web/20080513120524/http://www.ac.wwu.edu/~vawter/PhysicsNet/Topics/Dynamics/Forces/DragCoeficientValues.html
     */
    private final double coefficient = 0.5 * 0.47;

    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {

        for(Body bodyA: bodies.getBodies()) {

            if(!(bodyA instanceof Planet)) continue;
            AtmosphereModel atmosphere = ((Planet) bodyA).getAtmosphereModel();
            if(atmosphere instanceof NullAtmosphereModel) continue;

            for (Body bodyB: bodies.getBodies()) {

                double altitude = bodyA.getSurfaceToSurfaceDistance(bodyB);
                if(altitude > atmosphere.getHeight()) continue;

                Vector relativeVelocity = bodyB.getRelativeVelocity(bodyA);
                Vector flowDirection = relativeVelocity.unitVector();
                double flowSpeed = relativeVelocity.getLength();
                double atmosphereDensity = atmosphere.getDensity(altitude);

                // assumes the body is a sphere
                double area = Math.pow(bodyB.getRadius(), 2) * Math.PI;

                // See: https://en.wikipedia.org/wiki/Drag_equation
                Vector drag = flowDirection.product(coefficient * Math.pow(flowSpeed, 2) * atmosphereDensity * area);
                effects.addEffect(bodyA, new Effect(drag, new Vector()));

            }

        }

    }
}
