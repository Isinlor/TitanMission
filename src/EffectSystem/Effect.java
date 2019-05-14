package EffectSystem;

import Simulation.Vector;

public class Effect {

    private Vector force;
    private Vector torque;

    public Effect(Vector force, Vector torque) {
        this.force = force;
        this.torque = torque;
    }

    public Effect addEffect(Effect effect) {
        return new Effect(
            force.sum(effect.getForce()),
            torque.sum(effect.getTorque())
        );
    }

    public Vector getForce() {
        return force;
    }

    public Vector getTorque() {
        return torque;
    }

}
