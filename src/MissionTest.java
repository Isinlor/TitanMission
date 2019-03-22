import java.awt.*;

@SuppressWarnings("Duplicates")
public class MissionTest {

    private static final double timeStep = 60.0; // in s
    private static final long steps = (long) (300 * 24 * 60 * 60 / timeStep); // around 1 year
    private static final long stepsPerFrame = (long) (24 * 60 * 60 / timeStep); // around 1 day

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {
        optimize(new Bodies(CSVReader.readPlanets()));
    }

    private static void optimize(Bodies bodies) throws Exception {
        Body target;
        String sourceName = "Earth";
        String targetName = "Titan";
        double targetRadius = 1737400;
        double minDistance = Double.MAX_VALUE;
        double bestDistance = Double.MAX_VALUE;
        Body source;

        // make probe orbit the earth; notice that time step must be sufficiently small
        source = bodies.getBody(sourceName);
        double sourceRadius = 6371 * 1000;
        Double distanceFromCenter = sourceRadius;
        Body<BodyMetaSwing> probePrototype = getProbeInOrbit(source, distanceFromCenter);
        Body minProbe = probePrototype;

        int noProgress = 0;
        double range = 100000;
        while (true) {

            // try random steps from high range
            double step = range / Math.pow(10, 3 * Math.random());

            Bodies testBodies = bodies.copy();
            source = testBodies.getBody(sourceName);

            Bodies probes = getProbePrototypes(source, probePrototype, step);

            Bodies initProbes = probes.copy();
            testBodies.addBodies(probes);

            Bodies animateBodies = testBodies.copy();
            Bodies saveBodies = testBodies.copy();
            Tuple<Double, Body> tuple = getMinDistance(testBodies, targetName, probes);

            minDistance = tuple.getX();
            minProbe = initProbes.getBody(tuple.getY().getName());

            double astronomicalUnits = 1.496e11;
            long distanceInMarsRadii = Math.round(minDistance / targetRadius);

            // if we flyby closer than mars radius, then we have a direct hit
            if (minDistance < targetRadius) {
                System.out.println("Hit!");
                saveBodies.getBody(minProbe.getName()).rename(probePrototype.getName() + " HIT!");
                getSimulation(saveBodies).save("./resources/simulation-" + targetName + ".txt");
                System.exit(1);
            }

            if (minDistance < bestDistance) {
                bestDistance = minDistance;
                probePrototype = minProbe;

                System.out.print("Updated! ");
                System.out.print(distanceInMarsRadii + "\t" + Math.round(step));

                System.out.println("  relative speed: " + Math.round(probePrototype.getRelativeVelocity(bodies.getBody(sourceName)).getLength() / 1000) + " km/s");

                getSimulation(saveBodies).save("./resources/simulation-" + targetName + ".txt");

                noProgress = 0;

                animate(animateBodies);
            } else {
                System.out.print("         ");
                System.out.println(distanceInMarsRadii + "\t" + Math.round(step) + "\t" + noProgress);
                noProgress++;
            }

            if (noProgress > 1) {
                range = range / 2;
                noProgress = 0;
                System.out.println("Range updated to " + range);
            }

        }
    }

    private static Bodies getProbePrototypes(Body source, Body<BodyMetaSwing> probePrototype, double step) {
        Body probe;
        Bodies probes = new Bodies<BodyMetaSwing>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    probe = probePrototype.copy();
                    probe.rename("Probe (" + i + " " + j + " " + k + ")");

                    Vector stepUpdate = new Vector(
                            i * step,
                            j * step,
                            k * step
                    );

                    probe.addVelocity(stepUpdate);

                    // avoid crazy high probe velocities in relation to Earth
                    if (probe.getRelativeVelocity(source).getLength() > 50000) {
                        continue;
                    }

                    // not enough to escape Earth
                    if (probe.getRelativeVelocity(source).getLength() < 10000) {
                        continue;
                    }

                    probes.addBody(probe);
                }
            }
        }
        return probes;
    }

    private static Body<BodyMetaSwing> getProbeInOrbit(Body body, Double distance) {
        Double orbitalSpeed = Math.sqrt(SimulationSolarSystem.G * body.getMass() / distance);
        Vector position = body.getPosition().sum(new Vector(1.0, 0.0, 0.0).product(distance));
        Vector velocity = new Vector(0.0, 1.0, 0.0).product(orbitalSpeed).sum(body.getVelocity());

        return new Body<>(
                "probe",
                position,
                velocity,
                1,
                new BodyMetaSwing(Color.gray)
        );
    }

    private static Tuple<Double, Body> getMinDistance(Bodies testBodies, String targetName, Bodies<BodyMeta> probes) {

        Body target;
        Tuple<Double, Body> bestTuple = new Tuple<>(Double.MAX_VALUE, null);

        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < steps; i++) {

            testBodies.iterate(timeStep);

            target = testBodies.getBody(targetName);

            for (Body probe : probes.getBodies()) {

                // adding speed limit makes probe fly further away from sun
                double probeSpeed = probe.getVelocity().getLength();
                if (probeSpeed > 1000000) {
                    testBodies.removeBody(probe);
                    probes.removeBody(probe);
                }

                double distance = probe.computeDistance(target);

                if (distance < minDistance) {
                    minDistance = distance;
                    bestTuple = new Tuple<>(minDistance, probe);
                }

            }

        }
        return bestTuple;
    }

    private static void animate(Bodies bodies) {
        getSimulation(bodies).start();
    }

    private static Simulation getSimulation(Bodies bodies) {
        return new Simulation(bodies, steps, timeStep, stepsPerFrame, 0.4e10);
    }
}
