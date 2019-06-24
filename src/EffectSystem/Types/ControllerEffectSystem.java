package EffectSystem.Types;

import ControlSystem.Command;
import ControlSystem.Controllable;
import EffectSystem.*;
import Simulation.*;

public class ControllerEffectSystem implements EffectSystem {
    public void collectEffects(Bodies bodies, Effects effects, double timeStep) {
        for (Body body: bodies.getBodies()) {
            if(!(body instanceof Controllable)) continue;
            if(!(body instanceof RotatingBody)) continue;
            Command command = ((Controllable) body).getCommand(timeStep);
            Vector thrust = new Vector(0, -command.getThrust(), 0)
                .rotateAroundAxisZ(new Vector(), ((RotatingBody)body).getAngularDisplacement().z);
            Vector torque = new Vector(0, 0, command.getTorque());
            effects.addEffect(body, new Effect(thrust, torque));
            if (body instanceof Spacecraft) {
                double fuelMassRate = thrust.getLength()/( ((Spacecraft) body).getSpecificImpulse() * Constants.G0);
                double changeOfMass=fuelMassRate*timeStep;
                ((Spacecraft) body).setFuelMass(((Spacecraft) body).getFuelMass()-Math.abs(changeOfMass));
                if (((Spacecraft) body).getFuelMass()<0)
                    System.out.println("No fuel left!");

            }
        }
    }
}
