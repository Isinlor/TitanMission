import javax.swing.*;
import java.util.*;

public class GeneticSimulation {
    static final int ELITENUM = 2;
    static final int POPSIZE = 3;
    static final Random generator = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {
        Spacecraft[] population = new Spacecraft[POPSIZE];

        Random randomGenerator = new Random();

        LinkedList<Planet> planets = CSVReader.readPlanets();

        //
        for (int i = 0; i < POPSIZE; i++) {
            Vector velocity = new Vector(randomGenerator.nextDouble() * 200000 - 100000, randomGenerator.nextDouble() * 200000 - 100000, 0);
            population[i] = new Spacecraft("First try " + i, 1, planets.get(1).getPosition().sum(new Vector(planets.get(1).getRadius(), planets.get(1).getRadius(), planets.get(1).getRadius())), velocity, planets.get(8));
        }

        int generation = 0;
        while (generation < 100) {
            planets = CSVReader.readPlanets();

            Bodies bodies = new Bodies();
            for (Planet planet: planets) bodies.addBody(planet);
            for (Spacecraft spacecraft: population) {
                spacecraft.setGoal(planets.get(8));
                bodies.addBody(spacecraft);
            }

            System.setProperty("sun.java2d.opengl", "true");

            JFrame window = new JFrame();

            // exit after clicking close button
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            SimulationPanel simulationPanel = new SimulationPanel(5e9, bodies);

            window.setContentPane(simulationPanel);

            window.pack();

            // make display visible
            window.setVisible(true);

            /*simulationPanel.startAnimation(
                    (Bodies<BodyMetaSwing> bodies1) -> {
                        for (int i = 0; i < 24; i++) {
                            bodies1.iterate(60*60);
                        }
                    }
            );*/

            System.out.println(bodies);

            // simulate time span of 1 year
            for (int i = 0; i < 365*2; i++) {
                bodies.iterate(60*60*24);
            }

            System.out.println(bodies);

            Thread.sleep(5000);
            window.setVisible(false);


            Spacecraft[] parents = topSelection(population);

            //create new generation
            for (int i = 0; i < population.length; i++) {
                population[i] = crossoverAverage(parents[generator.nextInt(parents.length)], parents[generator.nextInt(parents.length)]);
                mutation(population[i]);
            }
            generation++;
        }
    }

    // first crossover method
    public static Spacecraft crossoverAverage(Spacecraft p1, Spacecraft p2) {
        Random randomGenerator = new Random();
        Spacecraft child = new Spacecraft("Child " + randomGenerator.nextInt(9999999),  p1.getMass(), p1.getStartingPosition(), p1.getStartingVelocity().sum(p2.getStartingVelocity()).product(0.5), p1.getGoal());
        return child;
    }

    // one random mutation per individual in chance of a certain probability
    public static void  mutation(Spacecraft p) {
        p.addVelocity(new Vector(p.getStartingVelocity().x * (generator.nextDouble()-0.5) * 0.05,
                p.getStartingVelocity().y * (generator.nextDouble()-0.5) * 0.05,
                p.getStartingVelocity().z * (generator.nextDouble()-0.5) * 0.05));
    }

    // elitist selection method
    public static Spacecraft[] topSelection(Spacecraft[] population) {
        Arrays.sort(population);
        System.out.println(population[0].getShortestDistance());
        System.out.println(population[0].getStartingVelocity());
        Spacecraft[] parents = new Spacecraft[ELITENUM];
        for (int i = 0; i < ELITENUM; i++) {
            parents[i] = population[i].clone();
        }
        return parents;
    }
}
