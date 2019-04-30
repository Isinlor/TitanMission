package ControlSystem;

/**
 * Allows to give commands based only on time.
 *
 * https://en.wikipedia.org/wiki/Open-loop_controller
 */
public interface OpenLoopController {

    /**
     * Computes command for the appropriate time.
     *
     * @param time Absolute time.
     *
     * @return The command to execute.
     */
    Command getCommand(double time);

}