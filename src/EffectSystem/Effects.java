package EffectSystem;

import ControlSystem.Command;
import ControlSystem.Controllable;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.RotatingBody;
import Simulation.Vector;

import java.util.LinkedHashMap;

public class Effects {

    Bodies bodies;
    LinkedHashMap<String, Effect> effects = new LinkedHashMap<>();

    public Effects(Bodies bodies) {
        this.bodies = bodies;
        for (Body body: bodies.getBodies()) {
            effects.put(body.getName(), new NullEffect());
        }
    }

    public void addEffect(Body body, Effect effect) {
        if(body.getMeta().has("noEffects")) return;
        effects.put(body.getName(), effect.addEffect(effects.get(body.getName())));
    }

    public Effect getEffect(Body body) {
        return effects.get(body.getName());
    }

}
