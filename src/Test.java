import ODESolvers.LeapfrogODE;
import Simulation.*;

@SuppressWarnings("Duplicates")
public class Test {

    /**
     * Some simple test.
     */
    public static void main(String[] args) {

        Bodies bodies = new Bodies();

        double auToM = 1.496e11;
        double dayToSecond = 1.0 / 86400.0;

        // HORIZONS data for the 16th of March 2019 00:00 with SSB as origin
        Body sun = new Body(
            "sun",
            new Vector(-1.351343105506232E-03*auToM, 7.549817138203992E-03*auToM, -4.200718115315673E-05*auToM),
            new Vector(-8.222950279730839E-06*auToM*dayToSecond, 1.252598675779703E-06*auToM*dayToSecond, 2.140020605610505E-07*auToM*dayToSecond),
            1.988435e30);
        bodies.addBody(sun);
        Body earth = new Body(
            "earth",
            new Vector(-9.918696803493554E-01*auToM, 9.679454643549934E-02*auToM, -4.277240997129137E-05*auToM),
            new Vector(-1.825836604899280E-03*auToM*dayToSecond, -1.719621912926312E-02*auToM*dayToSecond, 3.421794164900239E-07*auToM*dayToSecond),
            5.9721986e24);
        bodies.addBody(earth);
        Body mars = new Body(
            "mars",
            new Vector(2.341284054032922E-01*auToM,  1.537313782783677E+00*auToM, 2.623394307816976E-02*auToM),
            new Vector(-1.331027418143195E-02*auToM*dayToSecond,  3.319493802125395E-03*auToM*dayToSecond, 3.961351541925593E-04*auToM*dayToSecond),
            6.41693e23);
        bodies.addBody(mars);
        
        System.out.println(bodies);

        // simulate time span of 10 days
        for (int i = 0; i < 60*60*24*10; i++) {
            new LeapfrogODE().iterate(bodies, 1);
        }

        System.out.println(bodies);
    }

}