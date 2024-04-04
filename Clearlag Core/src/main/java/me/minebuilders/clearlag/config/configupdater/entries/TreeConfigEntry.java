package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bob7l
 */
public class TreeConfigEntry implements ConfigEntry {

    private final String key;

    private final List<ConfigEntry> entries = new ArrayList<>();

    public TreeConfigEntry(String key) {
        this.key = key;
    }

    public void addConfigEntry(ConfigEntry entry) {
        this.entries.add(entry);
    }

    public void removeConfigEntry(ConfigEntry entry) {
        this.entries.remove(entry);
    }

    public void merge(ConfigEntry mergingEntry) {

        if (mergingEntry instanceof TreeConfigEntry) {
            merge((TreeConfigEntry) mergingEntry);
        }
    }

    public void merge(TreeConfigEntry mergingTree) {

        for (ConfigEntry entry : entries) {

            for (ConfigEntry mergingEntry : mergingTree.entries) {

                if (entry.getKey().equals(mergingEntry.getKey()))
                    entry.merge(mergingEntry);
            }
        }
    }

    @Override
    public Object getValue() {
        return entries;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public void write(BufferedWriter writer, int tabs) throws IOException {

        for (int i = 0; i < tabs; ++i)
            writer.write(ConfigEntry.TAB);

        writer.write(key + ":");

        writer.newLine();

        for (ConfigEntry entry : entries) {
            entry.write(writer, tabs + 1);
        }
    }
}
