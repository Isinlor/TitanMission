import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileSystem {
    static boolean exists(String location) {
        return Files.exists(Paths.get(location));
    }
    static String read(String location) {
        try {
            return new String(Files.readAllBytes(Paths.get(location)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to read from location " + location, e);
        }
    }
    static void write(String location, String content) {
        try {
            Path path = Paths.get(location);
            if(!Files.exists(path)) {
                Files.createFile(path);
            }
            Files.write(path, content.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to write to resource " + location, e);
        }
    }

    static String tryLoadResource(String resource) {
        URL url = FileSystem.class.getClassLoader().getResource(resource);
        if(url == null) return null;
        if(exists(url.getFile())) return read(url.getFile());
        return null;
    }

}
