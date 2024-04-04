package me.minebuilders.clearlag.config.configupdater.entries;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * @author bob7l
 */
public interface ConfigEntry {

    String TAB = "  ";

    String getKey();

    Object getValue();

    void merge(ConfigEntry value);

    void write(BufferedWriter writer, int tabs) throws IOException;

}
