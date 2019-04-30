import Utilities.CSVReader;
import Visualisation.SimulationPanel;
import javax.swing.*;
import Simulation.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GeneticSimulation {

    private static JFrame window = new JFrame();
    private static final double timeStep = 60*60; // in s
    private static final long steps = (long)(365*24*60*60 / timeStep);
    private static final long stepsPerFrame = (long)(12*60*60 / timeStep);
    private static long animatedSteps;

    static final int ELITENUM = 2;
    static final int POPSIZE = 3;
    static final Random generator = new Random(System.currentTimeMillis());

    public static void main(String[] args) throws InterruptedException {
        Spacecraft[] population = new Spacecraft[POPSIZE];

        Random randomGenerator = new Random();

        LinkedList<Body> planets = CSVReader.readPlanets();

        Body earth = planets.get(1);
        Body goal = planets.get(2); // 2 is Mars, 8 is Neptune (seeing Neptune requires changing scale in animate to 10e9)
        for (int i = 0; i < POPSIZE; i++) {
            Vector velocity = new Vector(randomGenerator.nextDouble() * 200000 - 100000, randomGenerator.nextDouble() * 200000 - 100000, 0);
            population[i] = new Spacecraft("First try " + i, 1, earth.getPosition().sum(new Vector(earth.getRadius(), earth.getRadius(), earth.getRadius())), velocity, goal);
        }

        int generation = 0;
        double minDistance = Double.MAX_VALUE;
        System.out.println("Generation #: min dist in goal radii; this population dist in goal radii");
        while (generation < 1000) {
            planets = CSVReader.readPlanets();

            Bodies bodies = new Bodies();
            for (Body planet: planets) bodies.addBody(planet);
            for (Spacecraft spacecraft: population) {
                spacecraft.setGoal(goal);
                bodies.addBody(spacecraft);
            }

            System.setProperty("sun.java2d.opengl", "true");

            Bodies bodiesToAnimate = bodies.copy();

            for (int i = 0; i < steps; i++) {
                bodies.simulate(timeStep);
            }

//            System.out.println(bodies);

            Spacecraft[] parents = topSelection(population);

            Arrays.sort(population);
            Double distance = population[0].getShortestDistance();

            //create new generation
            for (int i = 0; i < population.length; i++) {
                population[i] = crossoverAverage(parents[generator.nextInt(parents.length)], parents[generator.nextInt(parents.length)]);
                mutation(population[i]);
            }

            generation++;

            double radius = goal.getRadius();
            long distanceInGoalRadii = Math.round(distance / radius);
            long minDistanceInGoalRadii = Math.round(minDistance / radius);
            if(distance < minDistance) {
                minDistance = distance;
                System.out.println("\nBetter spacecraft found! " + distanceInGoalRadii + "\n");
                animate(bodiesToAnimate);
            } else {
                System.out.println("Generation " + generation + ": " + minDistanceInGoalRadii + "\t" + distanceInGoalRadii);
            }

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
//        System.out.println(population[0].getShortestDistance());
//        System.out.println(population[0].getStartingVelocity());
        Spacecraft[] parents = new Spacecraft[ELITENUM];
        for (int i = 0; i < ELITENUM; i++) {
            parents[i] = population[i].clone();
        }
        return parents;
    }

    private static void animate(Bodies bodies) throws InterruptedException {

        // exit after clicking close button
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        SimulationPanel simulationPanel = new SimulationPanel(1e9, bodies);
        window.setContentPane(simulationPanel);
        window.pack();
        window.setVisible(true);
        animatedSteps = 0;

        simulationPanel.startSimulation(
            (Bodies bodies2) -> {
                if(animatedSteps > steps) {
                    simulationPanel.pauseSimulation();
                    window.dispose();
                }
                for (int i = 0; i < stepsPerFrame; i++) {
                    bodies2.simulate(timeStep);
                }
            }
        );
    }

}
