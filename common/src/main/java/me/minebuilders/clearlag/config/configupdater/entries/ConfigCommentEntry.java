package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author bob7l
 */
public record ConfigCommentEntry(String comment) implements ConfigEntry {

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
