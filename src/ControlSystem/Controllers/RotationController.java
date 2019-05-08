package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.*;
import Utilities.Logger.ConsoleLogger;
import Utilities.Logger.Logger;
import Utilities.Logger.NullLogger;
import Utilities.Utils;

import java.util.function.Function;

/**
 * This controller allows spacecraft to rotate towards angle requested by target angle function.
 */
public class RotationController implements Controller {

    /**
     * Change to ConsoleLogger if you want to do debugging.
     */
    static Logger logger = new NullLogger();

    Function<Spacecraft, Double> targetAngleFunction;

    private RotationController(Function<Spacecraft, Double> targetAngleFunction) {
        this.targetAngleFunction = targetAngleFunction;
    }

    public Command getCommand(Spacecraft spacecraft, double timeStep) {

        double targetAngle = targetAngleFunction.apply(spacecraft);
        double spacecraftAngle = spacecraft.getAngularDisplacement().z;
        double spacecraftAngularVelocity = spacecraft.getAngularVelocity().z;

        // We need to compute the smallest change that will move spacecraft angle to target angle
        // See: https://stackoverflow.com/questions/1878907/the-smallest-difference-between-2-angles
        double signedDistance = Utils.mod(targetAngle - spacecraftAngle + Math.PI, Utils.TAU) - Math.PI;

        // when signed distance is high, the spacecraft will increase velocity until reaching the target
        // this means that it will almost certainly overshoot, due to the high velocity that needs to be cancelled
        // this process of overshooting and counteracting may lead to continuous oscillation (instability)
        // taking into account the current velocity allows to reduce the torque, and dampen the oscillation
        // see: https://en.wikipedia.org/wiki/PID_controller#Derivative
        // see: https://en.wikipedia.org/wiki/PID_controller#Control_damping
        // to consider: Should this be based on recorded error?
        double predictedChange = spacecraftAngularVelocity * timeStep;
        double neededChange = signedDistance - predictedChange;

        logger.log(
            "\ttarget: " + Math.toDegrees(targetAngle) +
            "\tcurrent: " + Math.toDegrees(spacecraftAngle) +
            "\tdistance: " + Math.toDegrees(signedDistance) +
            "\tpredicted: " + Math.toDegrees(predictedChange) +
            "\tneeded change: " + Math.toDegrees(neededChange)
        );

        return new Command(0,  neededChange * 0.001);

    }

    /**
     * Creates controller that allows spacecraft to maintain constant angle towards the surface of another body.
     *
     * @param body The body with a surface.
     * @param angle The angle relative to the surface.
     *
     * @return The controller.
     */
    public static RotationController createMaintainAngleToSurfaceController(Body body, double angle) {
        return new RotationController((Spacecraft spacecraft) -> {
            Vector targetPosition = body.getPosition();
            Vector spacecraftPosition = spacecraft.getPosition();

            // move coordinates, so that in this frame target is at coordinate (0, 0)
            // the transformation preserves relative position
            // this will allow to compute clock angle - see below
            Vector spacecraftPositionWithTargetAtCenter = spacecraftPosition.difference(targetPosition);

            // computes clock angle between the body and a spacecraft
            // see Utils.clockAngle documentation for explanation of "clock angle" concept
            return Utils.clockAngle(
                spacecraftPositionWithTargetAtCenter.x,
                -spacecraftPositionWithTargetAtCenter.y // FIXME: y-axis reversed (swing)
            ) + angle;
        });
    }

    /**
     * Creates controller that allows spacecraft to maintain constant angle with regard to it's own velocity.
     *
     * @param angle The angle relative to the velocity.
     *
     * @return The controller.
     */
    public static RotationController createMaintainAngleToVelocityController(double angle) {
        return new RotationController((Spacecraft spacecraft) -> {
            Vector velocity = spacecraft.getVelocity();
            double velocityAngle = Utils.clockAngle(velocity.x, -velocity.y); // FIXME: y-axis reversed (swing)
            logger.log("Velocity angle: " + Math.toDegrees(velocityAngle));
            return  velocityAngle + angle;
        });
    }

}
