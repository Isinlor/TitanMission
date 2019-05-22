package Utilities;

import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows to store and retrieve recordings.
 *
 * @param <R> An type of the record. It must be serializable.
 */
public class Recording<R extends Serializable> {

    private List<R> recording;

    public Recording(List<R> recording) {
        this.recording = recording;
    }

    public List<R> getRecording() {
        return recording;
    }

    public void save(String location) {
        try {
            FileSystem.write(Paths.get(location), serialize());
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize recording to location " + location, e);
        }
    }

    public static <T extends Serializable> Recording load(String location, Class<T> classReference) {
        try {
            return unserialize(FileSystem.read(location), classReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to unserialize recording from location " + location, e);
        }
    }

    public String serialize() {
        StringBuilder stringBuilder = new StringBuilder();
        for (R record: recording) {
            stringBuilder.append(record.serialize()).append("\n");
        }
        return stringBuilder.toString();
    }

    public static <T extends Serializable> Recording unserialize(String string, Class<T> classReference) {
        try {

            Method unserialize = classReference.getDeclaredMethod("unserialize", String.class);

            List<T> recording = new ArrayList<>();
            String[] serializedRecords = string.trim().split("(\\r\\n|\\r|\\n)");
            for(String serializedRecord: serializedRecords) {
                @SuppressWarnings({"JavaReflectionInvocation", "unchecked"})
                T record = (T)unserialize.invoke(serializedRecord, (Object[])null);
                recording.add(record);
            }

            return new Recording<T>(recording);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
