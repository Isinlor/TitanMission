package Simulation.SolarSystem;

import Simulation.Atmosphere.SimpleAtmosphereModel;
import Simulation.Planet;
import Simulation.Vector;
import Utilities.Metadata;

public class Titan extends Planet {

    public Titan() {
        super(
            "Titan",
            new Vector(),
            new Vector(),
            1.3452E23, 2575000.0,
            new Metadata(),
            new SimpleAtmosphereModel(-9.72259e-6, 0.483279)
        );
    }

}
