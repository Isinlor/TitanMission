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

    private RotationController verticalLandingController = RotationController.createMaintainAngleToSurfaceController(Math.PI);

    public DestinationController(double maxThrust) {
        this.maxThrust = maxThrust;
        rotationController = new RotationController((Spacecraft spacecraft) -> {

            double approachSpeed = spacecraft.getApproachSpeed(spacecraft.getTarget());
            double distance = spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());
            double sumOfRadii = spacecraft.getRadius() + spacecraft.getTarget().getRadius();

            Vector attraction = spacecraft.computeAttraction(spacecraft.getTarget());
            Vector relativeVelocity = spacecraft.getRelativeVelocity(spacecraft.getTarget());
            Vector relativePosition = spacecraft.getRelativePosition(spacecraft.getTarget());
            Vector relativeDirection = relativePosition.unitVector();

            // whether a target is being approached:
            // 1 means closing distance, 0 stationary, -1 getting further away
            double xApproach = -Math.signum(relativePosition.x * relativeVelocity.x);
            double yApproach = -Math.signum(relativePosition.y * relativeVelocity.y);

            // surface to surface distance in x and y coordinates
            double xDistance = Math.abs(relativePosition.x - sumOfRadii);
            double yDistance = Math.abs(relativePosition.y - sumOfRadii);

            // deceleration needed to stop in x and y coordinate combined
            // not totally sure: combined deceleration can be sometimes bigger than sum of x and y decelerations
            double decelerationToStop = attraction.getLength() + (approachSpeed * approachSpeed) / (2 * distance);

            // deceleration to stop in one of the coordinates
            double xDecelerationToStop = (relativeVelocity.x * relativeVelocity.x) / (2 * xDistance);
            double yDecelerationToStop = (relativeVelocity.y * relativeVelocity.y) / (2 * yDistance);

            // take into account gravity
            xDecelerationToStop += attraction.getLength();
            yDecelerationToStop += attraction.getLength();

            // this part allows to start breaking
            // the breaking should start when spacecraft is reaching maximum speed that still allows to decelerate to 0
            if(approachSpeed > 0 && decelerationToStop >= maxThrust) {
                // decelerationToStop sometimes is bigger than x + y decelerations to stop (?)
                if(xApproach > 0) xApproach = -xApproach;
                if(yApproach > 0) yApproach = -yApproach;
            } else {
                if(xApproach > 0 && xDecelerationToStop >= maxThrust) {
                    xApproach = -xApproach;
                }

                if(yApproach > 0 && yDecelerationToStop >= maxThrust) {
                    yApproach = -yApproach;
                }
            }


            // select angles of thrust based on x and y approach variables

            if(yApproach < 0 && xApproach >= 0) {
                return Utils.clockAngle(0, -relativeVelocity.y);
            }

            if(yApproach >= 0 && xApproach < 0) {
                return Utils.clockAngle(relativeVelocity.x, 0);
            }

            if(yApproach < 0 && xApproach < 0) {
                return Utils.clockAngle(relativeVelocity.x, -relativeVelocity.y);
            }

            if(yApproach >= 0 && xApproach >= 0) {
                return Utils.clockAngle(relativePosition.x, -relativePosition.y);
            }

            return 0.0;

        });
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        double torque = rotationController.getCommand(spacecraft, timeStep).getTorque();

        Body target = spacecraft.getTarget();

        double thrust;

        double gravity = target.computeAttraction(spacecraft).getLength();
        double altitude = target.getSurfaceToSurfaceDistance(spacecraft);
        double approachSpeed = target.getApproachSpeed(spacecraft);
        double decelerationToStop = gravity + (approachSpeed * approachSpeed) / (2 * altitude);

        if(approachSpeed > 0 && decelerationToStop > maxThrust) {
            thrust = decelerationToStop; // a cheat to allow safe landing
        } else {
            thrust = maxThrust;
        }

        // takes control over spacecraft orientation in last phase of approach
        if(approachSpeed * timeStep * 3 > altitude || altitude < 0.1) {
            torque = verticalLandingController.getCommand(spacecraft, timeStep).getTorque();
        }

        if(altitude < 0.1) {
            return new Command(0.0, torque);
        }

        return new Command(thrust, torque);

    }

}
