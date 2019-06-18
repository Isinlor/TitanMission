package Simulation.Atmosphere;

/**
 * An interface for atmosphere model.
 */
public interface AtmosphereModel {

    /**
     * Returns density of the atmosphere at the given altitude.
     *
     * @param altitude The altitude in meters.
     *
     * @return Density in kg/m^3.
     */
    double getDensity(double altitude);

    /**
     * Where atmosphere is considered to end.
     *
     * @return Atmosphere height in meters.
     */
    double getHeight();

}
