package ControlSystem;

import Simulation.Spacecraft;

/**
 * Allows to give commands to a spaceship.
 *
 * The implementing controller can be either open loop or closed loop.
 * https://en.wikipedia.org/wiki/Control_theory#Open-loop_and_closed-loop_(feedback)_control
 *
 * Why are open and closed loop controllers not separate?
 * Spaceship is given as an argument the controller in order to reduce complexity that would be related to
 * differentiating between open and closed loop controllers. The differentiation would require two interfaces and two
 * implementations of spacecrafts that take controller as an argument.
 */
public interface Controller {

    /**
     * Computes command based on the state of the spaceship.
     *
     * @param spacecraft The full state of a spacecraft.
     *                   An open loop controller should relay just on the internal time of the spacecraft.
     * @param timeStep The length of time step when the command will be applied in seconds.
     *
     * @return The foreseen command for this spacecraft at this moment.
     */
    Command getCommand(Spacecraft spacecraft, double timeStep);

}