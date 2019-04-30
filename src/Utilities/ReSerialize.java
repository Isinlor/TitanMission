package Utilities;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class ReSerialize {

    public static void main(String[] args) {
        for (String location: FileSystem.listFiles(args[0])) {
            try {
                Simulation.load(location).save(location);
                System.out.println("✔ Location " + location);
            } catch (Exception e) {
                System.out.println("✗ Location " + location);
            }
        }
    }

}
