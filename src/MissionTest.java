import Simulation.*;
import Utilities.*;
import Visualisation.*;

@SuppressWarnings("Duplicates")
public class MissionTest {

    private static Simulation simulation;

    private static final double timeStep = 60.0; // in s
    private static final long steps = (long)(100*24*60*60 / timeStep); // around 1 year
    private static final long stepsPerFrame = (long)(24*60*60 / timeStep); // around 1 day

    private static String resourcesPath;

    /**
     * Some simple test.
     */
    public static void main(String[] args) throws Exception {

        resourcesPath = args[0];
        Bodies bodies = Bodies.unserialize(FileSystem.tryLoadResource("planets.txt"));
        simulation = new Simulation(bodies, steps, timeStep, stepsPerFrame, 0.4e10);
        optimize(bodies);

    }

    private static void optimize(Bodies bodies) throws Exception {
        Body source;
        Body target;
        String sourceName = "Earth";
        String targetName = "Mars";
        source = bodies.getBody(sourceName);
        target = bodies.getBody(targetName);
        double sourceRadius = source.getRadius();
        double targetRadius = target.getRadius();

        double minDistance = Double.MAX_VALUE;
        double bestDistance = Double.MAX_VALUE;

//        for (int i = 0; i < (long)(100*24*60*60 / timeStep); i++) {
//            bodies.iterate(timeStep);
//        }

        Double distanceFromCenter = sourceRadius + 100.0 * 1000.0;
        Body probePrototype = getProbeInOrbit(source, distanceFromCenter);
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
            long distanceInTargetRadii = Math.round(minDistance / targetRadius);

            // if we flyby closer than mars radius, then we have a direct hit
            if (minDistance < targetRadius) {
                System.out.println("Hit!");
                saveBodies.getBody(minProbe.getName()).rename(probePrototype.getName() + " HIT!");
                try {
                    simulation.save(resourcesPath + "/simulation-" + targetName + ".txt");
                } catch (Exception e) {
                    System.out.println(simulation.serialize());
                }
                System.exit(1);
            }

            if (minDistance < bestDistance) {
                bestDistance = minDistance;
                probePrototype = minProbe;

                System.out.print("Updated! ");
                System.out.print(distanceInTargetRadii + "\t" + Math.round(step));

                System.out.println("  relative speed: " + Math.round(probePrototype.getRelativeVelocity(bodies.getBody(sourceName)).getLength() / 1000) + " km/s");

                simulation.save(resourcesPath + "/simulation-" + targetName + ".txt");

                noProgress = 0;

                animate(animateBodies);
            } else {
                System.out.print("         ");
                System.out.println(distanceInTargetRadii + "\t" + Math.round(step) + "\t" + noProgress);
                noProgress++;
            }

            if(noProgress > 3 && range > 1) {
                range = range / 2;
                noProgress = 0;
                System.out.println("Range updated to " + range);
            }

        }
    }

    private static Bodies getProbePrototypes(Body source, Body probePrototype, double step) {
        Body probe;
        Bodies probes = new Bodies();
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

                    // avoid crazy high probe velocities in relation to source
                    if(probe.getRelativeVelocity(source).getLength() > source.computeSecondEscapeVelocity(probe) * 2) {
                        continue;
                    }

                    // not enough to escape source
                    if(probe.getRelativeVelocity(source).getLength() < source.computeSecondEscapeVelocity(probe)) {
                        continue;
                    }

                    probes.addBody(probe);
                }
            }
        }
        if(probes.getBodies().isEmpty()) probes.addBody(probePrototype.copy());
        return probes;
    }

    private static Body getProbeInOrbit(Body body, Double distance) {
        Double orbitalSpeed = Math.sqrt(Constants.G * body.getMass() / distance);
        Vector position = body.getPosition().sum(new Vector(1.0, 0.0, 0.0).product(distance));
        Vector velocity = new Vector(0.0, 1.0, 0.0).product(orbitalSpeed).sum(body.getVelocity());

        return new Body(
            "probe",
            position,
            velocity,
            1//, new Metadata(Color.gray)
        );
    }

    private static Tuple<Double, Body> getMinDistance(Bodies testBodies, String targetName, Bodies probes) {

        Body target;
        Tuple<Double, Body> bestTuple = new Tuple<>(Double.MAX_VALUE, null);

        double minDistance = Double.MAX_VALUE;
        for (int i = 0; i < steps; i++) {

            testBodies.iterate(timeStep);

            target = testBodies.getBody(targetName);

            for (Body probe : probes.getBodies()) {

                // adding speed limit makes probe fly further away from sun
                double probeSpeed = probe.getVelocity().getLength();
                if(probeSpeed > 100000) {
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
        simulation.setBodies(bodies);
    }

}
