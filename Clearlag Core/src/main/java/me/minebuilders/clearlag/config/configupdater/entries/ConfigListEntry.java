package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author bob7l
 */
public class ConfigListEntry implements ConfigEntry {

    private final String key;

    private List<Object> values = new ArrayList<>();

    public ConfigListEntry(String key, Object... values) {
        this.key = key;
        this.values.addAll(Arrays.asList(values));
    }

    public void add(Object obj) {
        values.add(obj);
    }

    public void remove(Object obj) {
        values.remove(obj);
    }

    @Override
    public Object getValue() {
        return values;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void merge(ConfigEntry entry) {

        if (entry.getValue() instanceof List)
            values = (List<Object>) entry.getValue();
        else
            values = new ArrayList<>(1);
    }

    @Override
    public void write(BufferedWriter writer, int tabs) throws IOException {

        final StringBuilder tabLine = new StringBuilder();

        for (int i = 0; i < tabs; ++i)
            tabLine.append(ConfigEntry.TAB);

        writer.write(tabLine + key + ":");

        if (values.isEmpty())
            writer.write(" []");
        else {
            tabLine.append(ConfigEntry.TAB);

            for (Object value : values) {

                writer.newLine();

                writer.write(tabLine + value.toString());
            }
        }

        writer.newLine();
    }
}
