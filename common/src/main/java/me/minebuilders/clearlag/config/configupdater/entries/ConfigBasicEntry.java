package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author bob7l
 */
public class ConfigBasicEntry implements ConfigEntry {

    private final String key;

    private Object value;

    public ConfigBasicEntry(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void merge(ConfigEntry entry) {
        value = entry.getValue();
    }

    @Override
    public void write(BufferedWriter writer, int tabs) throws IOException {

        for (int i = 0; i < tabs; ++i) {
            writer.write(ConfigEntry.TAB);
        }

        writer.write(key + ":" + value);

        writer.newLine();
    }
}
