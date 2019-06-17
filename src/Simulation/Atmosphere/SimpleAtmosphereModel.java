package Simulation.Atmosphere;

/**
 * Very simple atmosphere math model.
 *
 * Atmosphere density: 10 ^ (intercept + slope*altitude) kg/m^3
 * Atmosphere is assumed to end when density goes below 10^-10 kg/m^3
 */
public class SimpleAtmosphereModel implements AtmosphereModel {

    private final double slope;
    private final double intercept;
    private final double height;

    public SimpleAtmosphereModel(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;

        // we define end of atmosphere when atmosphere density goes below 10^-10 kg/m^3
        this.height = ((intercept * Math.log(10)) + Math.log(Math.pow(10, -10)))/(slope * Math.log(10));
    }

    public double getDensity(double altitude) {
        return Math.pow(10, intercept+slope*altitude);
    }

    public double getHeight() {
        return height;
    }

}