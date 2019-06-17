package Simulation.Atmosphere;

public class NullAtmosphereModel implements AtmosphereModel {
    public double getDensity(double altitude) {
        return 0;
    }
    public double getHeight() {
        return 0;
    }
}
