package Utilities.Logger;

/**
 * Allows to log information to console.
 */
public class ConsoleLogger implements Logger {
    public void log(String info) {
        System.out.println(info);
    }
}
