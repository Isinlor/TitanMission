package ControlSystem;

/**
 * Command that indicates to do nothing.
 */
public class NullCommand extends Command {
    public NullCommand() {
        super(0, 0);
    }
}