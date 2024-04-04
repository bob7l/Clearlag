package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;

/**
 * Created by TCP on 2/6/2016.
 */
public class ColoredStringsCV implements ConfigData<String[]> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public String[] getValue(String path) {

        final String[] lines = configHandler.getConfig().getString(path).split("/n");

        for (int i = 0; i < lines.length; ++i) {
            lines[i] = Util.color(lines[i]);
        }

        return lines;
    }

}