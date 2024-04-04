package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;

/**
 * Created by TCP on 2/6/2016.
 */
public class ColoredStringCV implements ConfigData<String> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public String getValue(String path) {
        return Util.color(configHandler.getConfig().getString(path));
    }

}