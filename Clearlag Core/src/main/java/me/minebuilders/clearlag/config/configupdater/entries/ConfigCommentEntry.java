package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author bob7l
 */
public class ConfigCommentEntry implements ConfigEntry {

    private final String comment;

    public ConfigCommentEntry(String comment) {
        this.comment = comment;
    }

    @Override
    public Object getValue() {
        return comment;
    }

    @Override
    public String getKey() {
        return comment;
    }

    @Override
    public void merge(ConfigEntry entry) {
    }

    @Override
    public void write(BufferedWriter writer, int tabs) throws IOException {

        writer.write(comment);

        writer.newLine();
    }
}
