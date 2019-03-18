import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("Duplicates")
public class MissionTest {

    static JFrame window = new JFrame();
    static final double timeStep = 60.0*10.0; // in s
    static final long steps = (long)(300*24*60*60 / timeStep); // around 1 year

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        // speeds up things on ubuntu significantly
        // comment out if it does not work on windows / macos
        System.setProperty("sun.java2d.opengl", "true");

        // make display visible
        window.setVisible(true);

        Bodies<BodyMetaSwing> bodies = new Bodies<>();

        double auToM = 1.496e11;
        double dayToSecond = 1.0 / 86400.0;

        // HORIZONS data for the 16th of March 2019 00:00 with SSB as origin
        bodies.addBody(new Body<BodyMetaSwing>(
            "sun",
            new Vector(-1.351343105506232E-03*auToM, 7.549817138203992E-03*auToM, -4.200718115315673E-05*auToM),
            new Vector(-8.222950279730839E-06*auToM*dayToSecond, 1.252598675779703E-06*auToM*dayToSecond, 2.140020605610505E-07*auToM*dayToSecond),
            1.988435e30,
            new BodyMetaSwing(Color.yellow)
        ));

        bodies.addBody(new Body<BodyMetaSwing>(
            "earth",
            new Vector(-9.918696803493554E-01*auToM, 9.679454643549934E-02*auToM, -4.277240997129137E-05*auToM),
            new Vector(-1.825836604899280E-03*auToM*dayToSecond, -1.719621912926312E-02*auToM*dayToSecond, 3.421794164900239E-07*auToM*dayToSecond),
            5.9721986e24,
            new BodyMetaSwing(Color.blue)
        ));

        bodies.addBody(new Body<BodyMetaSwing>(
            "mars",
            new Vector(2.341284054032922E-01*auToM,  1.537313782783677E+00*auToM, 2.623394307816976E-02*auToM),
            new Vector(-1.331027418143195E-02*auToM*dayToSecond,  3.319493802125395E-03*auToM*dayToSecond, 3.961351541925593E-04*auToM*dayToSecond),
            6.41693e23,
            new BodyMetaSwing(Color.red)
        ));

        bodies.addBody(new Body<BodyMetaSwing>(
            "probe",
            bodies.getBody("earth").getPosition().sum(new Vector(10, 10, 0).product(1000.0*1000.0)),
            bodies.getBody("earth").getVelocity(),
            1,
            new BodyMetaSwing(Color.gray)
        ));

//        bodies.getBody("probe").addVelocity(
//            new Vector(11000, 11000)
//        );
//        animate(bodies);

//        System.out.println(isHit(bodies, "mars", 24*1000, 60*60));


//        display(bodies);

        optimize(bodies);

    }

    private static void animate(Bodies bodies) throws InterruptedException {
//        double time = System.currentTimeMillis() - 1000;
        for (int i = 0; i < steps; i++) {
            bodies.iterate(timeStep);

            if(i % 50 == 0) {
                display(bodies);
                Thread.sleep(2);
            }

//            if(System.currentTimeMillis() - time > 1) {
//                time = System.currentTimeMillis();
//                Thread.sleep(2);
//            }
        }
    }

    private static void optimize(Bodies bodies) throws Exception {
        Body probe;
        Body target;
        String targetName = "mars";
        double bestDistance = Double.MAX_VALUE;
        Vector bestInitVelocity = new Vector();

        double step = 10000; // * Math.random();
        while(true) {

            Vector bestStepUpdate = new Vector();
            double bestStepDistance = Double.MAX_VALUE;
            for (int i = -10; i < 10; i = i + 5) {
                for (int j = -10; j < 10; j = j + 5) {

                    Bodies testBodies = bodies.copy();
                    probe = testBodies.getBody("probe");
                    Body earth = testBodies.getBody("earth");

                    Vector stepUpdate = new Vector(
                        i * step,
                        j * step
                    );

                    Vector initVelocity = bestInitVelocity.sum(stepUpdate);
                    probe.addVelocity(initVelocity);

                    // avoid crazy high probe velocities in relation to Earth
                    if(probe.getVelocity().sum(earth.getVelocity().product(-1)).getLength() > 30000) {
                        continue;
                    }

                    double minDistance = isHit(testBodies, targetName);

                    // if we flyby closer than mars radius, then we have a direct hit
                    if(minDistance < 3389*1000) {
                        System.out.println(
                            initVelocity.x + ", " +
                            initVelocity.y + ", " +
                            initVelocity.z
                        );
                        System.exit(1);
                    }

                    if(bestStepDistance > minDistance) {
                        bestStepDistance = minDistance;
                        bestStepUpdate = stepUpdate;
                    }

                }
            }

            if(bestStepDistance < bestDistance) {
                bestInitVelocity = bestInitVelocity.sum(bestStepUpdate);
                bestDistance = bestStepDistance;
                System.out.println("Updated!");

                Bodies animateBodies = bodies.copy();
                animateBodies.getBody("probe").addVelocity(bestInitVelocity);
                animate(animateBodies);
            }

            step = step / 1.1;

            System.out.println(bestDistance / 1.496e11 + " " + step);

        }
    }

    private static double isHit(Bodies testBodies, String targetName) {

        Body probe;
        Body target;

        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < steps; i++) {

            testBodies.iterate(timeStep);

            probe = testBodies.getBody("probe");
            target = testBodies.getBody(targetName);

            if(probe.getVelocity().getLength() > 60000) {
                return Double.MAX_VALUE;
            }

            double distance = probe.computeDistance(target);
            minDistance = Math.min(minDistance, distance);

        }

        return minDistance;

    }

    static void display(Bodies bodies) {

        // exit after clicking close button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.setContentPane(
            new SimulationPanel(
                0.8e9,
                 bodies
            )
        );

        window.pack();

    }

}
