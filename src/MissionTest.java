import javax.swing.*;
import java.awt.*;

@SuppressWarnings("Duplicates")
public class MissionTest {

    private static JFrame window = new JFrame();
    private static final double timeStep = 60.0; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep); // around 1 year
    private static final long stepsPerFrame = (long)(24*60*60 / timeStep); // around 1 day
    private static long animatedSteps;

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

//        System.out.println(getMinDistance(bodies.copy(), "mars"));
//        animate(bodies.copy());

//        display(bodies);

        optimize(bodies);

    }

    private static void optimize(Bodies bodies) throws Exception {
        Body probe;
        Body target;
        String targetName = "mars";
        double minDistance = Double.MAX_VALUE;
        double bestDistance = Double.MAX_VALUE;
        Vector bestInitVelocity = new Vector();
        Body earth;

        // make probe orbit the earth; notice that time step must be sufficiently small
        earth = bodies.getBody("earth");
        Double distanceFromCenter = 6371 * 1000.0 + 100.0 * 1000.0;
        Double orbitalSpeed = Math.sqrt(Simulation.G * earth.getMass() / distanceFromCenter);
        Vector position = earth.getPosition().sum(new Vector(1.0, 0.0, 0.0).product(distanceFromCenter));
        Vector velocity = new Vector(0.0, 1.0, 0.0).product(orbitalSpeed).sum(earth.getVelocity());

        Body probePrototype = new Body<BodyMetaSwing>(
            "probe",
            position,
            velocity,
            1,
            new BodyMetaSwing(Color.gray)
        );

        while(true) {

            // try random steps from high range
            double step = 10000 / Math.pow(10, 3*Math.random());

            Bodies testBodies = bodies.copy();
            earth = testBodies.getBody("earth");

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {

                    probe = probePrototype.copy();

                    Vector stepUpdate = new Vector(
                        i * step,
                        j * step
                    );

                    Vector initVelocity = bestInitVelocity.sum(stepUpdate);
                    probe.addVelocity(initVelocity);

                    // avoid crazy high probe velocities in relation to Earth
                    if(probe.getVelocity().sum(earth.getVelocity().product(-1)).getLength() > 100000) {
                        continue;
                    }

                }
            }

            minDistance = getMinDistance(testBodies, targetName);

            double astronomicalUnits = 1.496e11;
            double marsRadius = 3389.5 * 1000;
            long distanceInMarsRadii = Math.round(minDistance / marsRadius);

            // if we flyby closer than mars radius, then we have a direct hit
            if(minDistance < 3389*1000) {
                System.out.println("Hit!");
                System.exit(1);
            }

            if(minDistance < bestDistance) {
                bestDistance = minDistance;

                System.out.print("Updated! ");
                System.out.println(distanceInMarsRadii + "\t" + Math.round(step) + " " + bestInitVelocity.getLength());

                Bodies animateBodies = bodies.copy();
                animateBodies.getBody("probe").addVelocity(bestInitVelocity);
                animate(animateBodies);
            } else {
                System.out.print("         ");
                System.out.println(distanceInMarsRadii + "\t" + Math.round(step) + " " + bestInitVelocity.getLength());
            }

        }
    }

    private static double getMinDistance(Bodies testBodies, String targetName) {

        Body probe;
        Body target;
        Body bestProbe;

        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < steps; i++) {

            testBodies.iterate(timeStep);

            probe = testBodies.getBody("probe");
            target = testBodies.getBody(targetName);

            // adding speed limit makes probe fly further away from sun
//            double probeSpeed = probe.getVelocity().getLength();
//            if(probeSpeed > 100000) {
//                return Double.MAX_VALUE;
//            }

            double distance = probe.computeDistance(target);

            if(distance < minDistance) {
                minDistance = distance;
                bestProbe = probe;
            }

        }

        return new Topple<Double, Body>(minDistance, bestProbe);

    }

    private static void animate(Bodies bodies) throws InterruptedException {

        // exit after clicking close button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SimulationPanel simulationPanel = new SimulationPanel(0.7e9, bodies);
        window.setContentPane(simulationPanel);
        window.pack();
        window.setVisible(true);
        animatedSteps = 0;

        simulationPanel.startAnimation(
            (Bodies<BodyMetaSwing> bodies2) -> {
                if(animatedSteps > steps) {
                    simulationPanel.stopAnimation();
                    window.dispose();
                }
                for (int i = 0; i < stepsPerFrame; i++) {
                    bodies2.iterate(timeStep);
                }
            }
        );
    }

}
