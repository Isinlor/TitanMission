package Utilities.Logger;

/**
 * Allows to ignore logged information and reduce information overload.
 */
public class NullLogger implements Logger {
    public void log(String info) {
    }
}
