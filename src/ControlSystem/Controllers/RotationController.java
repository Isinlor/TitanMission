package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.*;
import Utilities.Utils;

import java.util.function.Function;

/**
 * This controller allows spacecraft to rotate towards angle requested by target angle function.
 */
public class RotationController implements Controller {

    Function<Spacecraft, Double> targetAngleFunction;

    private RotationController(Function<Spacecraft, Double> targetAngleFunction) {
        this.targetAngleFunction = targetAngleFunction;
    }

    public Command getCommand(Spacecraft spacecraft) {

        double targetAngle = targetAngleFunction.apply(spacecraft);
        double spacecraftAngle = spacecraft.getAngularDisplacement().z;

        // We need to compute the smallest change that will move spacecraft angle to target angle
        // See: https://stackoverflow.com/questions/1878907/the-smallest-difference-between-2-angles
        double signedDistance = Utils.mod(targetAngle - spacecraftAngle + Math.PI, Utils.TAU) - Math.PI;

        return new Command(0,  signedDistance * 0.0001);

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
            );
        });
    }

}
