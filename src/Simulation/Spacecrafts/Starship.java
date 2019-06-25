package Simulation.Spacecrafts;

import ControlSystem.Controller;
import Simulation.Spacecraft;

/**
 * https://en.wikipedia.org/wiki/Starship_(spacecraft)
 */
public class Starship extends Spacecraft {

    public Starship(String name, String targetName, Controller controller) {

        super(name, targetName, controller);

        setMass(150*1000);          // dry mass
        setRadius(55/2);            // 55 meters height
        setFuelMass(1950*1000);     // gross mass without dry mass
        setSpecificImpulse(380);    // ISP for one raptor engine in vacuum
        setMaxThrust(11500*7*1000); // 7 raptor engines

    }

}
