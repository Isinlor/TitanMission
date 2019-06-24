package EffectSystem.Types;

import EffectSystem.EffectSystem;
import EffectSystem.*;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Vector;
import Utilities.Utils;

import java.util.Random;

public class AtmosphereEffectSystem implements EffectSystem {

    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {

        for (Body body: bodies.getBodies()) {

            if(bodies.getHeaviestBody() == body) continue;

            double angle = Math.random() * Utils.TAU;

            Random random = new Random();

            double intensity = 10;
            if(body.getSurfaceToSurfaceDistance(bodies.getHeaviestBody()) < 10000) intensity = 5;
            if(body.getSurfaceToSurfaceDistance(bodies.getHeaviestBody()) < 1000) intensity = 1;
            if(body.getSurfaceToSurfaceDistance(bodies.getHeaviestBody()) < 100) intensity = 0.3;
            if(body.getSurfaceToSurfaceDistance(bodies.getHeaviestBody()) < 10) continue;

            Vector wind = new Vector(random.nextGaussian(), random.nextGaussian()).rotateAroundAxisZ(new Vector(), angle).product(intensity * Math.random());
            effects.addEffect(body, new Effect(wind, new Vector(0, 0, Math.random() / (6000 / intensity))));

        }

    }

}
