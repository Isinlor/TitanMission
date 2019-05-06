package ControlSystem.Controllers;

import ControlSystem.Command;
import ControlSystem.Controller;
import Simulation.Spacecraft;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class KeyboardController implements Controller {

    HashMap<Integer, Boolean> pressedKeys = new HashMap<>();

    public KeyboardController() {
        KeyboardFocusManager
            .getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(new KeyEventDispatcher() {
                public boolean dispatchKeyEvent(KeyEvent event) {
                    switch (event.getID()) {
                        case KeyEvent.KEY_PRESSED:
                            pressedKeys.put(event.getKeyCode(), true);
                            break;
                        case KeyEvent.KEY_RELEASED:
                            pressedKeys.remove(event.getKeyCode());
                            break;
                    }
                    return false;
                }
            });
    }

    public Command getCommand(Spacecraft spacecraft) {

        int thrustDirection = 0;
        if(pressedKeys.containsKey(KeyEvent.VK_UP) || pressedKeys.containsKey(KeyEvent.VK_W)) {
            thrustDirection = 1;
        }

        int torqueDirection = 0;
        if(pressedKeys.containsKey(KeyEvent.VK_RIGHT) || pressedKeys.containsKey(KeyEvent.VK_D)) {
            torqueDirection = 1;
        } else if(pressedKeys.containsKey(KeyEvent.VK_LEFT) || pressedKeys.containsKey(KeyEvent.VK_A)) {
            torqueDirection = -1;
        }

        return new Command(thrustDirection * 10, torqueDirection * 0.00001);

    }

}