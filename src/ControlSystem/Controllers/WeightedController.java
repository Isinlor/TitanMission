package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Spacecraft;

import java.util.function.Function;

/**
 * Weighted controller allows to adjust thrust and torque based on some function.
 *
 * It should be useful when composing many controllers together.
 */
public class WeightedController implements Controller {

    private Controller controller;
    private Function<Spacecraft, Double> thrustWeightFunction;
    private Function<Spacecraft, Double> torqueWeightFunction;

    public WeightedController(
        Controller controller,
        Function<Spacecraft, Double> thrustWeightFunction,
        Function<Spacecraft, Double> torqueWeightFunction
    ) {
        this.controller = controller;
        this.thrustWeightFunction = thrustWeightFunction;
        this.torqueWeightFunction = torqueWeightFunction;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {
        Double thrustWeight = thrustWeightFunction.apply(spacecraft);
        Double torqueWeight = torqueWeightFunction.apply(spacecraft);
        if(thrustWeight == 0 && torqueWeight == 0) return new NullCommand();
        return controller
            .getCommand(spacecraft, timeStep)
            .weight(
                thrustWeight,
                torqueWeight
            );
    }

    public static WeightedController createConstantThrustWeightController(
        Controller controller,
        double thrustWeight,
        Function<Spacecraft, Double> torqueWeightFunction
    ) {
        return new WeightedController(controller, (s) -> { return thrustWeight; }, torqueWeightFunction);
    }

}
