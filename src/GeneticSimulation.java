import javax.swing.*;
import java.util.*;

/**
 * Some very basic stuff to get you started. It shows basically how each
 * chromosome is built.
 *
 * @author Jo Stevens
 * @author Alard Roebroeck
 * @author Frederick van der Windt
 * @author Moritz Gehlhaar
 * @version 18.12.2018
 */

public class GeneticSimulation {
    static final double MUTATIONPROB = 0.2;
    static final int ELITENUM = 2;
    static final int POPSIZE = 3;
    static final Random generator = new Random(System.currentTimeMillis());

    /**
     * @param args
     */


    public static void main(String[] args) throws InterruptedException {
        GA();
    }

    public static int GA() throws InterruptedException {
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
        return generation;
    }

    // first crossover method
    public static Spacecraft crossoverAverage(Spacecraft p1, Spacecraft p2) {
        Random randomGenerator = new Random();
        Spacecraft child = new Spacecraft("Child " + randomGenerator.nextInt(9999999),  p1.getMass(), p1.getStartingPosition(), p1.getStartingVelocity().sum(p2.getStartingVelocity()).product(0.5), p1.getGoal());
        return child;
    }

    /* // first crossover method
    public static Spacecraft crossoverSplitting(Spacecraft p1, Spacecraft p2) {
        int splittingPoint = generator.nextInt(TARGET.length());
        Individual child = new Individual(new char[TARGET.length()]);
        for (int i = 0; i < TARGET.length(); i++)
            if (i < splittingPoint)
                child.chromosome[i] = p1.chromosome[i];
            else
                child.chromosome[i] = p2.chromosome[i];

        return child;
    }

    //second crossover method
    public static Spacecraft crossoverRandom(Spacecraft p1, Spacecraft p2) {
        char[] childChromosome = new char[TARGET.length()];
        char[] motherChromosome = p1.getChromosome();
        char[] fatherChromosome = p2.getChromosome();
        for (int j = 0; j < childChromosome.length; j++) {
            if (generator.nextBoolean()) {
                childChromosome[j] = motherChromosome[j];
            } else {
                childChromosome[j] = fatherChromosome[j];
            }
        }
        return new Individual(childChromosome);
    }

    // mutation of every chromosome with a certain probability
    public static void mutation(Spacecraft p) {
        for (int i = 0; i < p.chromosome.length; i++) {
            if (generator.nextDouble() <= MUTATIONPROB) {
                p.chromosome[i] = alphabet[generator.nextInt(alphabet.length)];
            }
        }
    }*/

    /*
    // one random mutation per individual in chance of a certain probability
    public static void mutation2(Individual p) {
        if (generator.nextDouble() <= MUTATIONPROB)
            p.chromosome[generator.nextInt(TARGET.length())] = alphabet[generator.nextInt(alphabet.length)];
    }*/

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
/*
    // roulette selection method
    public static Spacecraft[] rouletteSelection(Spacecraft[] population) {
        // get overall fitness
        double sumOfFitness = 0;
        for (Spacecraft i : population)
            sumOfFitness += i.getSm() * i.getFitness();

        Individual[] parents = new Individual[ELITENUM];
        for (int i = 0; i < ELITENUM; i++) {
            do {
                Individual individual = population[generator.nextInt(population.length)];
                if (generator.nextFloat() < ((individual.getFitness() * individual.getFitness()) / sumOfFitness)) {
                    parents[i] = individual.clone();
                    break;
                }
            } while (parents[i] == null);
        }

        return parents;
    }*/
}
