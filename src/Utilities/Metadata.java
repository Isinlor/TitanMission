package Utilities;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple interface allowing to hold some special "metadata" values.
 *
 * It may be colors, textures, spheres etc.
 *
 * It is intended to be easily serializable.
 */
public class Metadata {

    private Map<String, String> keyValueMap = new LinkedHashMap<>();

    public Metadata() {
    }

    private Metadata(Map<String, String> keyValueMap) {
        this.keyValueMap.putAll(keyValueMap);
    }

    public String get(String key) {
        return keyValueMap.get(key);
    }

    public boolean has(String key) {
        return keyValueMap.containsKey(key);
    }

    public void set(String key, String value) {
        keyValueMap.put(key, value);
    }

    /**
     * For repeated simulation bodies may need to be copied.
     *
     * This copy should make sure that copy can exists independently from source.
     * In other words copy and source must not influence each other to avoid one breaking another.
     */
    public Metadata copy() {
        return new Metadata(keyValueMap);
    }

    public String serialize() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry: keyValueMap.entrySet()) {
            builder.append(" <" + entry.getKey() + "=" + entry.getValue() + "> ");
        }
        return builder.toString();
    }

    public static Metadata unserialize(String string) {
        Pattern pattern = Pattern.compile(" <(?<key>[a-zA-Z]+?)=(?<value>.+?)> ");
        Matcher matcher = pattern.matcher(string);

        Metadata metadata = new Metadata();
        while (matcher.find()) {
            metadata.set(matcher.group("key"), matcher.group("value"));
        }
        return metadata;
    }

    public String toString() {
        return serialize();
    }

}
