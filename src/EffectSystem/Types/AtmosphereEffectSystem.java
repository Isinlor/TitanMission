package EffectSystem.Types;

import EffectSystem.EffectSystem;
import EffectSystem.*;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Vector;
import Utilities.Utils;

public class AtmosphereEffectSystem implements EffectSystem {

    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {

        for (Body body: bodies.getBodies()) {

            if(body.getName().equals("Titan")) continue;

            double angle = Math.random() * Utils.TAU;

            double intensity = 10;
            if(body.getSurfaceToSurfaceDistance(bodies.getHeaviestBody()) < 100) intensity = 1;

            Vector wind = new Vector(Math.random(), Math.random() + 5).rotateAroundAxisZ(new Vector(), angle).product(intensity * Math.random());
            effects.addEffect(body, new Effect(wind, new Vector(0, 0, Math.random() / (6000 / intensity))));

        }

    }

}
