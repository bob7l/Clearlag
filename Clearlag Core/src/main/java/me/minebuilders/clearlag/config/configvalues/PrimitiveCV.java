package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;

/**
 * Created by TCP on 2/3/2016.
 */
public class PrimitiveCV implements ConfigData<Object> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public Object getValue(String path) {
        return configHandler.getConfig().get(path);
    }

}