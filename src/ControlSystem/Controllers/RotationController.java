package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import ControlSystem.NullCommand;
import Simulation.*;
import Utilities.Utils;

/**
 * This controller allows spacecraft to maintain constant angle towards the surface of another body.
 */
public class RotationController implements Controller {

    Body target;

    public RotationController(Body target) {
        this.target = target;
    }

    public Command getCommand(Spacecraft spacecraft) {

        Vector targetPosition = target.getPosition();
        Vector spacecraftPosition = spacecraft.getPosition();

        Vector spacecraftPositionWithTargetAtCenter = spacecraftPosition.difference(targetPosition);

        double clockAngle = Utils.clockAngle(
            spacecraftPositionWithTargetAtCenter.x,
            -spacecraftPositionWithTargetAtCenter.y // FIXME: y-axis reversed
        );

        double spacecraftAngle = spacecraft.getAngularDisplacement().z;

        // See: https://stackoverflow.com/questions/1878907/the-smallest-difference-between-2-angles
        double signedDistance = Utils.mod(clockAngle - spacecraftAngle + Math.PI, Utils.TAU) - Math.PI;

        return new Command(0,  signedDistance * 0.0001);

    }

}
