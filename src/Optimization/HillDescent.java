package Optimization;

import ODESolvers.ODESolver;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Spacecraft;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class HillDescent {

    double steps;
    double timeStep;
    ODESolver solver;

    Setup bestSetup;
    double bestFitness = Double.MAX_VALUE;
    Function<Setup, Setup> generator;
    Function<Spacecraft, Double> fitnessFunction;

    public HillDescent(
        double steps,
        double timeStep,
        ODESolver solver,
        Setup bestSetup,
        Function<Setup, Setup> generator,
        Function<Spacecraft, Double> fitnessFunction
    ) {
        this.steps = steps;
        this.timeStep = timeStep;
        this.solver = solver;
        this.bestSetup = bestSetup;
        this.generator = generator;
        this.fitnessFunction = fitnessFunction;
    }

    public double getBestFitness() {
        return bestFitness;
    }

    public Setup optimizationStep() {

        // generate new setup - the new setup will be evaluated
        Setup setup = generator.apply(bestSetup.copy());

        Setup testSetup = setup.copy(); // create copy for reference safety

        // combine environment with prototypes
        Bodies allBodies = testSetup.getAllBodies();

        // get set of references to prototypes from all bodies
        Set<Spacecraft> prototypes = new LinkedHashSet<>();
        for (Body prototypeSelector: testSetup.getPrototypes().getBodies()) {
            prototypes.add((Spacecraft)allBodies.getBody(prototypeSelector.getName()));
        }

        // start evaluating
        for (int i = 0; i < steps; i++) {

            solver.iterate(allBodies, timeStep);

            // evaluate each prototype separately
            for(Spacecraft prototype: prototypes) {

                double fitness = fitnessFunction.apply(prototype);

                if(fitness < bestFitness) {

                    // save best setup with the fresh version of the best prototype
                    bestSetup = setup.withNewBestPrototype(
                        (Spacecraft)testSetup.getPrototypes().getBody(prototype.getName())
                    );
                    bestFitness = fitness;

                }

                // one of advantages of saving references to prototypes is ability to evaluate their fitness after crash
                // however, there is no need to evaluate them multiple times after the crash, one is enough
                if(!allBodies.hasBody(prototype.getName())) {
//                    prototypes.remove(prototype);
                }

            }

        }

        return bestSetup.copy(); // return copy of best setup; again copy for reference safety

    }

}
