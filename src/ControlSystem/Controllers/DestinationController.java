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

import java.util.Map;
import java.util.function.Function;

public class DestinationController implements Controller {

    /**
     * Change to ConsoleLogger if you want to do debugging.
     */
    static Logger logger = new ConsoleLogger();

    private double maxThrust;

    private RotationController rotationController;

    private RotationController verticalLandingController = RotationController.createMaintainAngleToSurfaceController(Math.PI);

    private Function<Spacecraft, Body> targetFunction = Spacecraft::getTarget;

    public DestinationController(double maxThrust) {
        this.maxThrust = maxThrust;
        rotationController = new RotationController((Spacecraft spacecraft) -> {

            Body target = getTarget(spacecraft);

            double approachSpeed = spacecraft.getApproachSpeed(target);
            double distance = spacecraft.getSurfaceToSurfaceDistance(target);
            double sumOfRadii = spacecraft.getRadius() + target.getRadius();

            Vector attraction = spacecraft.getExternalForces();
            Vector relativeVelocity = spacecraft.getRelativeVelocity(target);
            Vector relativePosition = spacecraft.getRelativePosition(target);
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
            double decelerationToStop = (attraction.getLength() / spacecraft.getMass()) + (approachSpeed * approachSpeed) / (2 * distance);

            // deceleration to stop in one of the coordinates
            double xDecelerationToStop = (relativeVelocity.x * relativeVelocity.x) / (2 * xDistance);
            double yDecelerationToStop = (relativeVelocity.y * relativeVelocity.y) / (2 * yDistance);

            // take into account gravity
            xDecelerationToStop += (attraction.getLength() / spacecraft.getMass());
            yDecelerationToStop += (attraction.getLength() / spacecraft.getMass());


            // this part allows to start breaking
            // the breaking should start when spacecraft is reaching maximum speed that still allows to decelerate to 0
            double maxAcceleration = maxThrust / spacecraft.getMass();
            if(approachSpeed > 0 && decelerationToStop >= maxAcceleration) {
                // decelerationToStop sometimes is bigger than x + y decelerations to stop (?)
                if(xApproach > 0) xApproach = -xApproach;
                if(yApproach > 0) yApproach = -yApproach;
            } else {
                if(xApproach > 0 && xDecelerationToStop >= maxAcceleration) {
                    xApproach = -xApproach;
                }

                if(yApproach > 0 && yDecelerationToStop >= maxAcceleration) {
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

            if(yApproach >= 0 && xApproach >= 0 && decelerationToStop < maxAcceleration) {

                // simply accelerating directly towards target may not work if acceleration is small
                // in relation to the velocity; this piece of code is adjusting thrust
                // so that overtime it aligns velocity towards target
                double positionAngle = Utils.clockAngle(relativePosition.x, -relativePosition.y);
                double velocityAngle = Utils.clockAngle(relativeVelocity.x, -relativeVelocity.y);

                // the alignment is done by accelerating little bit away
                // from the component of relative velocity that is not pointing towards the target
                double signedDistance = Utils.mod(positionAngle - velocityAngle + Math.PI, Utils.TAU) - Math.PI;
                return Utils.clockAngle(relativePosition.x, -relativePosition.y) + 2 * signedDistance;

            }

            return Double.NaN;

        });
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        double torque = rotationController.getCommand(spacecraft, timeStep).getTorque();

        // takes control over spacecraft orientation in last phase of approach
        double realApproachSpeed = spacecraft.getApproachSpeed(spacecraft.getTarget());
        double realAltitude = spacecraft.getSurfaceToSurfaceDistance(spacecraft.getTarget());

        if(realAltitude < 10 && realApproachSpeed > 1) {
            targetFunction = Spacecraft::getTarget;
        }

        if((realAltitude < 1 && realApproachSpeed < 1) || (realAltitude < 3 && realAltitude > 1 && realApproachSpeed < 3)) {
            torque = verticalLandingController.getCommand(spacecraft, timeStep).getTorque();
            return new Command(0.0, torque);
        }

        Body target = getTarget(spacecraft);

        double thrust;

        double gravity = spacecraft.getExternalForces().getLength() / spacecraft.getMass();
        double altitude = target.getSurfaceToSurfaceDistance(spacecraft);
        double approachSpeed = target.getApproachSpeed(spacecraft);
        double decelerationToStop = gravity + (approachSpeed * approachSpeed) / (2 * altitude);

        if(approachSpeed > 0 && decelerationToStop > (maxThrust / spacecraft.getMass())) {
            thrust = decelerationToStop * spacecraft.getMass(); // a cheat to allow safe landing
        } else {
            thrust = maxThrust;
        }

        return new Command(thrust, torque);

    }

    Body getTarget(Spacecraft spacecraft) {
        return targetFunction.apply(spacecraft);
    }

    public static DestinationController createWithStaticTarget(Body target, double maxThrust) {
        DestinationController controller = new DestinationController(maxThrust);
        controller.targetFunction = (s) -> { return target; };
        return controller;
    }

}
