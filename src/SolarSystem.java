import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class SolarSystem extends Bodies {

    SolarSystem() {

        double auToM = 149597870.700 * 1000;
        double invDayToSecond = 1 / 86400.0;

        // Sun
        addBody(
            new Body(
                "Sun",
                new Vector(-1.351343105506232E-03,7.549817138203992E-03,-4.200718115315673E-05).product(auToM),
                new Vector(-8.222950279730839E-06,1.252598675779703E-06,2.140020605610505E-07).product(auToM * invDayToSecond),
                1988550000e21
            )
        );

        // Mars
        addBody(
            new Body(
                "Mars",
                new Vector(2.341284054032922E-01,1.537313782783677E+00,2.623394307816976E-02).product(auToM),
                new Vector(-1.331027418143195E-02,3.319493802125395E-03,3.961351541925593E-04).product(auToM * invDayToSecond),
                641.85e21
            )
        );

        // Earth
        addBody(
            new Body(
                "Earth",
                new Vector(-9.918696803493554E-01,9.679454643549934E-02,-4.277240997129137E-05).product(auToM),
                new Vector(-1.825836604899280E-03,-1.719621912926312E-02,3.421794164900239E-07).product(auToM*invDayToSecond),
                5973.6e21
            )
        );

        // Jupiter
        addBody(
            new Body(
                "Jupiter",
                new Vector(-1.619415486257015E+00,-5.067980606649805E+00,5.724354397991326E-02).product(auToM),
                new Vector(7.097138681149106E-03,-1.936475571366308E-03,-1.507099292363792E-04).product(auToM*invDayToSecond),
                1898600e21
            )
        );

        // Saturn
        addBody(
            new Body(
                "Saturn",
                new Vector(2.339322153829731E+00,-9.772708872601363E+00,7.680334653568568E-02).product(auToM),
                new Vector(5.118676949779789E-03,1.281165462656659E-03,-2.260252915431195E-04).product(auToM*invDayToSecond),
                568460e21
            )
        );

        // Titan
        addBody(
            new Body(
                "Titan",
                new Vector(2.340010336367727E+00,-9.765469982604248E+00,7.300390210773734E-02).product(auToM),
                new Vector(1.947323954493107E-03,1.729638118992622E-03,-1.417867687071718E-04).product(auToM*invDayToSecond),
                134.5e21
            )
        );

        // Space
        addBody(
                new Body(
                        "Spaceshuttle",
                        new Vector( -9.118696803493554E-01,9.279454643549934E-02,-4.477240997129137E-05).product(auToM),
                        new Vector(58389.77142673674, 66691.9949975113,0.0),
                        1000
                )
        );

    }

}
