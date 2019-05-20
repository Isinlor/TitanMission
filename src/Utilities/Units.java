package Utilities;

public class Units {

    public static final double AU = 149597870700.0;

    public static String speed(double metersPerSecond) {
        double absMetersPerSecond = Math.abs(metersPerSecond);
        if(absMetersPerSecond < 1) {
            return Utils.round(metersPerSecond * 100) + "cm/s";
        } else if(absMetersPerSecond < 1000) {
            return Utils.round(metersPerSecond) + "m/s";
        } else {
            return Utils.round(metersPerSecond / 1000) + "km/s";
        }
    }
    public static String distance(double meters) {
        if(meters < 1) {
            return Utils.round(meters * 100) + "cm";
        } else if(meters < 1000) {
            return Utils.round(meters) + "m";
        } else if(meters < AU) {
            return Utils.round(meters / 1000) + "km";
        } else {
            return Utils.round(meters / AU) + "au";
        }
    }
}
