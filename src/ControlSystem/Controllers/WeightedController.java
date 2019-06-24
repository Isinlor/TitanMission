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

    public static WeightedController createStartAtAltitudeController(
        Controller controller,
        double altitude
    ) {
        Function<Spacecraft, Double> function = (Spacecraft spacecraft) -> {
            return spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget()) < altitude ? 1. : 0.;
        };
        return new WeightedController(controller, function, function);
    }

    public static WeightedController createBetweenAltitudesController(
        Controller controller,
        double startAltitude,
        double endAltitude
    ) {
        Function<Spacecraft, Double> function = (Spacecraft spacecraft) -> {
            double altitude = spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());
            return  altitude > endAltitude && altitude < startAltitude ? 1. : 0.;
        };
        return new WeightedController(controller, function, function);
    }

}
