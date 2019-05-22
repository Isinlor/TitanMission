package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.Body;
import Simulation.Spacecraft;
import Simulation.Vector;
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

        if(!Utils.isRealNumber(targetAngle)) return new NullCommand();

        double spacecraftAngle = spacecraft.getAngularDisplacement().z;
        double spacecraftAngularSpeed = spacecraft.getAngularVelocity().z;

        // We need to compute the smallest change that will move spacecraft angle to target angle
        // See: https://stackoverflow.com/questions/1878907/the-smallest-difference-between-2-angles
        double signedDistance = Utils.mod(targetAngle - spacecraftAngle + Math.PI, Utils.TAU) - Math.PI;

        // This controller will try to achieve the target angle as soon as possible i.e. in the next time step
        // To cover the distance in the given time the spacecraft needs to have certain speed
        double neededSpeed = signedDistance / timeStep;

        // To achieve the needed speed, one needs to take into account the current speed and the moment of inertia
        // More explicitly:
        // needed speed = current speed + needed change in speed
        // needed change in speed = needed acceleration * time
        // needed acceleration = needed torque / moment of inertia
        // needed change in speed = (needed torque / moment of inertia) * time
        // needed speed = current speed + (needed torque / moment of inertia) * time
        // Rearranging the equation above gives the final equation for the needed torque below
        double neededTorque = (neededSpeed - spacecraftAngularSpeed) * spacecraft.getMomentOfInertia() / timeStep;

        logger.log(
            "\ttarget angle: " + Math.toDegrees(targetAngle) +
            "\tcurrent angle: " + Math.toDegrees(spacecraftAngle) +
            "\tdistance: " + Math.toDegrees(signedDistance) +
            "\tneeded speed: " + Math.toDegrees(neededSpeed) +
            "\tcurrent speed: " + Math.toDegrees(spacecraftAngularSpeed) +
            "\tneeded torque: " + Math.toDegrees(neededTorque)
        );

        return new Command(0,  neededTorque);

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

    /**
     * Creates controller that allows spacecraft to maintain constant angle with regard to the relative velocity.
     *
     * The relative velocity is computed as the velocity of a target as seen from the spacecraft.
     *
     * @param angle The angle with regard to the relative velocity of a target as seen from the spacecraft.
     *
     * @return The controller.
     */
    public static RotationController createMaintainAngleToRelativeVelocityController(double angle) {
        return new RotationController((Spacecraft spacecraft) -> {
            Vector velocity = spacecraft.getRelativeVelocity(spacecraft.getTarget());
            double velocityAngle = Utils.clockAngle(velocity.x, -velocity.y); // FIXME: y-axis reversed (swing)
            logger.log("Relative Velocity angle: " + Math.toDegrees(velocityAngle));
            return  velocityAngle + angle;
        });
    }

}
