package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Spacecraft;
import Simulation.Body;

/**
 * Suicide burn controller allows to slowdown spacecraft at the very last moment.
 * This is more or less method of landing that SpaceX is using.
 *
 * The burn will start at specified altitude with goal of reducing velocity to 0 when altitude reaches 0 as well.
 *
 * See: https://space.stackexchange.com/questions/10307/what-is-a-suicide-burn
 * See: https://www.youtube.com/watch?v=T3_Voh7NgDE
 */
public class SuicideBurnController implements Controller {

    private Body target;
    private double startAltitude;

    public SuicideBurnController(Body target, double startAltitude) {
        this.target = target;
        this.startAltitude = startAltitude;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        double altitude = target.computeDistance(spacecraft) - target.getRadius();
        if(altitude > startAltitude) return new NullCommand();

        double initialSpeed = target.getApproachSpeed(spacecraft);
        if(initialSpeed > 0) return new NullCommand(); // suicide burn should not be raising altitude

        // the spacecraft needs to overcome gravity in order to decelerate
        double gravity = target.computeAttraction(spacecraft).getLength();

        // Thrust must be setup so that final velocity is 0; see below for derivation:
        // https://www.reddit.com/r/KerbalAcademy/comments/4c42rz/maths_help_calculating_when_to_suicide_burn/d1f6xed/
        double thrust = gravity + (initialSpeed * initialSpeed) / (2 * altitude);

        return new Command(thrust, 0);

    }

}