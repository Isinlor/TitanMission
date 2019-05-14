package ControlSystem;

public interface Controllable {
    Command getCommand(double timeStep);
}
