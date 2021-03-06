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

    private double startAltitude;

    public SuicideBurnController(double startAltitude) {
        this.startAltitude = startAltitude;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        Body target = spacecraft.getTarget();
        double altitude = spacecraft.getSurfaceToSurfaceDistance(target);
        if(altitude > startAltitude) return new NullCommand();

        // the spacecraft needs to overcome gravity in order to decelerate
        double gravity = target.computeAttraction(spacecraft).getLength();
        double approachSpeed = target.getApproachSpeed(spacecraft);
        double relativeSpeed = spacecraft.getRelativeVelocity(target).getLength();

        // allows to avoid bouncing from surface at very low altitudes
        if(approachSpeed < 0 && altitude < 0.1) return new NullCommand();

        // landing with vertical speed less than 1 cm/s is ok!
        // relative speed is controlled so that suicide burn still works on very stable circular orbit
        if(approachSpeed < 0.01 && relativeSpeed < 0.1) return new NullCommand();

        // Thrust must be setup so that final velocity is 0; see below for derivation:
        // https://www.reddit.com/r/KerbalAcademy/comments/4c42rz/maths_help_calculating_when_to_suicide_burn/d1f6xed/
        double thrust = gravity + ((approachSpeed * approachSpeed) / (2 * altitude) * spacecraft.getMass());

        if(thrust < 0) return new NullCommand();

        return new Command(thrust, 0);

    }

}
