package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;

import java.util.HashMap;

/**
 * Created by TCP on 2/3/2016.
 */
public class WarnMapCV implements ConfigData<HashMap<Integer, String[]>> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public HashMap<Integer, String[]> getValue(String path) {

        final HashMap<Integer, String[]> warns = new HashMap<>();

        for (String line : configHandler.getConfig().getStringList(path)) {

            try {

                final String[] bits = line.split(" ", 2);

                final int time = Integer.parseInt(bits[0].replace("time:", ""));

                final String[] msg = Util.color(bits[1].replace("msg:", "")).split("/n");

                if (time > 0) {
                    warns.put(time, msg);
                } else {
                    Util.warning("Config Error at line " + path + ":");
                    Util.warning(line + " is an invalid warning!");
                }

            } catch (Exception e) {
                Util.warning("Config Read Error at line " + path + ":");

                if (e instanceof NumberFormatException) {
                    Util.warning("Failed to read 'time:' variable in " + line);
                    Util.warning("Insure you have a NUMBER after 'time:' specified");
                }
            }
        }

        return warns;
    }

}
