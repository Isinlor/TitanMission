package Optimization;

import Simulation.Bodies;
import Simulation.Spacecraft;

public class Setup {

    private Spacecraft bestPrototype;
    private Bodies prototypes;
    private Bodies environment;

    public Setup(Spacecraft bestPrototype, Bodies prototypes, Bodies environment) {
        this.bestPrototype = bestPrototype;
        this.prototypes = prototypes;
        this.environment = environment;
    }

    public Spacecraft getBestPrototype() {
        return bestPrototype;
    }

    public Bodies getPrototypes() {
        return prototypes;
    }

    public Bodies getEnvironment() {
        return environment;
    }

    public Bodies getAllBodies() {
        // combine environment with prototypes
        Bodies allBodies = getEnvironment().copy();
        allBodies.addBodies(getPrototypes());
        return allBodies;
    }

    public Setup withNewPrototypes(Bodies prototypes) {
        return new Setup(
            getBestPrototype(),
            prototypes,
            getEnvironment()
        );
    }

    public Setup withNewBestPrototype(Spacecraft newBestPrototype) {
        return new Setup(
            newBestPrototype,
            getPrototypes(),
            getEnvironment()
        );
    }

    public Setup copy() {
        return new Setup(
            getBestPrototype().copy(),
            getPrototypes().copy(),
            getEnvironment().copy()
        );
    }

}
