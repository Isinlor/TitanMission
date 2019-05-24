package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Bodies;
import Simulation.Body;
import Simulation.Spacecraft;
import Simulation.Vector;
import Utilities.Logger.ConsoleLogger;
import Utilities.Logger.Logger;
import Utilities.Logger.NullLogger;
import Utilities.Units;
import Utilities.Utils;

public class DestinationController implements Controller {

    /**
     * Change to ConsoleLogger if you want to do debugging.
     */
    static Logger logger = new ConsoleLogger();

    private double maxThrust;

    private RotationController rotationController;

    public DestinationController(double maxThrust) {
        this.maxThrust = maxThrust;
        rotationController = new RotationController((Spacecraft spacecraft) -> {

            Vector relativePosition = spacecraft.getRelativePosition(spacecraft.getTarget());
            double distance = spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());
            double approachSpeed = spacecraft.getApproachSpeed(spacecraft.getTarget());
            double maxAcceleration = maxThrust / spacecraft.getMass();
            double decelerationToStop = (approachSpeed * approachSpeed) / (2 * distance);

            if(approachSpeed > 0) {
                if(decelerationToStop > maxAcceleration) {
                    relativePosition = relativePosition.product(-1);
                }
                return Utils.clockAngle(relativePosition.x, -relativePosition.y); // FIXME: y-axis reversed (swing)
            } else {
                Vector velocity = spacecraft.getRelativeVelocity(spacecraft.getTarget());
                return Utils.clockAngle(velocity.x, -velocity.y); // FIXME: y-axis reversed (swing)
            }

        });
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        Body target = spacecraft.getTarget();

        double torque = rotationController.getCommand(spacecraft, timeStep).getTorque();

        return new Command(maxThrust, torque);

    }

}
