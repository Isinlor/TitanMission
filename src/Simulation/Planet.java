package Simulation;

import Simulation.Atmosphere.AtmosphereModel;
import Utilities.*;

public class Planet extends RotatingBody {

    Planet(String name, Vector position, Vector angularDisplacement, Vector velocity, Vector angularVelocity, double mass, double radius, Metadata meta, AtmosphereModel atmosphereModel) {
        super(name, position, angularDisplacement, velocity, angularVelocity, mass, radius, meta);
        this.atmosphereModel = atmosphereModel;
    }

    Planet(String name, Vector position, Vector velocity, double mass, double radius, Metadata meta, AtmosphereModel atmosphereModel) {
        super(name, position, velocity, mass, radius, meta);
        this.atmosphereModel = atmosphereModel;
    }

    AtmosphereModel atmosphereModel;

    public AtmosphereModel getAtmosphereModel() {
        return atmosphereModel;
    }

}
