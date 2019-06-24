package EffectSystem.Types;

import Simulation.*;
import EffectSystem.*;

import java.util.HashMap;
import java.util.Map;

public class WindEffectSystem implements EffectSystem {
    Map<Spacecraft, Wind> winds=new HashMap<Spacecraft, Wind>();
    @Override
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        for(Body bodyA: bodies.getBodies()) {
            if (bodyA instanceof Spacecraft) {
                for (Body bodyB : bodies.getBodies()) {
                    if (bodyB.getName().equals("titan")) {
                        if (getALtitude((Spacecraft) bodyA, bodyB) > 120000) {
                            Wind wind;
                            if (winds.containsKey(bodyA)) {
                                System.out.println("Yo");
                                winds.get(bodyA).changeWind();
                                wind = winds.get(bodyA);
                            } else {
                                wind = new Wind();
                                wind.getStrongWInd();
                                winds.put((Spacecraft) bodyA, wind);
                            }
                            //System.out.println("Strong" + wind.getWind().toString() + " length " + wind.getWind().getLength() + " altitide " + getALtitude((Spacecraft) bodyA, bodyB));
                            effects.addEffect(bodyA, new Effect(wind.getWind().quotient(100), new Vector()));
                        }
                        else{
                            Wind wind;
                            if (winds.containsKey(bodyA)) {
                                winds.get(bodyA).decreaseWind();
                                wind = winds.get(bodyA);
                            } else {
                                wind = new Wind();
                                winds.put((Spacecraft) bodyA, wind);
                            }
                            //System.out.println("Weak" + wind.getWind().toString() + " length " + wind.getWind().getLength() + " altitide " + getALtitude((Spacecraft) bodyA, bodyB));
                            effects.addEffect(bodyA, new Effect(wind.getWind().quotient(100), new Vector()));
                        }
                    }
                }
            }
            else if(bodyA.getName().equals("Titan")) {
                for (Body bodyB : bodies.getBodies()) {
                    if (bodyB instanceof Spacecraft) {
                        if (getALtitude((Spacecraft) bodyB, bodyA) > 120000) {
                            Wind wind;
                            if (winds.containsKey(bodyB)) {
                                winds.get(bodyB).changeWind();
                                wind = winds.get(bodyB);
                            } else {
                                wind = new Wind();
                                wind.getStrongWInd(); //CHECK
                                winds.put((Spacecraft) bodyB, wind);
                            }
                            //System.out.println("Strong" + wind.getWind().toString() + " length " + wind.getWind().getLength() + " altitide " + getALtitude((Spacecraft) bodyB, bodyA));
                            effects.addEffect(bodyB, new Effect(wind.getWind().quotient(100), new Vector()));
                        }
                        else{
                            Wind wind;
                            if (winds.containsKey(bodyB)) {
                                winds.get(bodyB).decreaseWind();
                                wind = winds.get(bodyB);
                            } else {
                                wind = new Wind();
                                winds.put((Spacecraft) bodyB, wind);
                            }
                            //System.out.println("Weak" + wind.getWind().toString() + " length " + wind.getWind().getLength() + " altitide " + getALtitude((Spacecraft) bodyB, bodyA));
                            effects.addEffect(bodyB, new Effect(wind.getWind().quotient(100), new Vector()));
                        }
                    }
                }
            }
        }
    }
    public double getALtitude(Spacecraft a, Body b){
        return (a.getPosition().euclideanDistance(b.getPosition())-b.getRadius());
    }
}
