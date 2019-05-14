package Utilities;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

import Simulation.*;
import Utilities.*;
import Visualisation.*;

public class FileSystem {

    public static boolean exists(URI location) {
        return Files.exists(Paths.get(location));
    }

    public static boolean exists(String location) {
        return Files.exists(Paths.get(location));
    }

    public static String read(URI location) {
        try {
            return new String(Files.readAllBytes(Paths.get(location)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from location " + location, e);
        }
    }

    public static String read(String location) {
        try {
            return new String(Files.readAllBytes(Paths.get(location)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from location " + location, e);
        }
    }

    public static void write(String location, String content) {
        try {
            Path path = Paths.get(location);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, content.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write to resource " + location, e);
        }
    }

    public static String tryLoadResource(String resource) {
        try {
            URI uri = FileSystem.class.getClassLoader().getResource(resource).toURI();
            if (exists(uri)) return read(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String[] listFiles(URI location) {
        if (!exists(location)) return new String[]{};
        return
                Arrays.stream(Objects.requireNonNull(
                        new File(location).listFiles()
                ))
                        .filter(File::isFile)
                        .map(File::getAbsolutePath)
                        .toArray(String[]::new);
    }

    public static String[] listFiles(String location) {
        if (!exists(location)) return new String[]{};
        return
                Arrays.stream(Objects.requireNonNull(
                        new File(location).listFiles()
                ))
                        .filter(File::isFile)
                        .map(File::getAbsolutePath)
                        .toArray(String[]::new);
    }

    public static File getFileResource(String resource) {
        URL url = FileSystem.class.getClassLoader().getResource(resource);
        if (url == null) throw new RuntimeException("Resource " + resource + " not found!");
        return new File(url.getFile());
    }

}
