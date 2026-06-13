package me.minebuilders.clearlag.config.configvalues;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.config.ConfigHandler;
import org.bukkit.Sound;

import java.util.HashMap;

/**
 * Created by TCP on 2/3/2016.
 */
public class WarnMapCV implements ConfigData<HashMap<Integer, Warning>> {

    @AutoWire
    private ConfigHandler configHandler;

    @Override
    public HashMap<Integer, Warning> getValue(String path) {
        final HashMap<Integer, Warning> warns = new HashMap<>();

        for (String line : configHandler.getConfig().getStringList(path)) {
            try {
                int time = -1;
                String msg = null;
                Sound sound = null;

                final int msgIndex = line.indexOf("msg:");
                if (msgIndex == -1) {
                    Util.warning("Config Read Error at line " + path + ":");
                    Util.warning("Missing 'msg:' in " + line);
                    continue;
                }

                msg = line.substring(msgIndex + "msg:".length());

                final String prefix = line.substring(0, msgIndex).trim();
                for (String token : prefix.split(" ")) {
                    if (token.isEmpty()) {
                        continue;
                    }

                    if (token.startsWith("time:")) {
                        time = Integer.parseInt(token.replace("time:", ""));
                    } else if (token.startsWith("sound:")) {
                        final String soundName = token.replace("sound:", "");
                        try {
                            sound = Sound.valueOf(soundName.toUpperCase());
                        } catch (IllegalArgumentException ex) {
                            Util.warning("Invalid sound '" + soundName + "' at line " + path + " in: " + line);
                            Util.warning("Use a valid Sound enum name for your server version.");
                        }
                    }
                }

                if (time > 0) {
                    final String[] messages = Util.color(msg).split("/n");
                    warns.put(time, new Warning(messages, sound));
                } else {
                    Util.warning("Config Error at line " + path + ":");
                    Util.warning(line + " is an invalid warning!");
                }
            } catch (Exception e) {
                Util.warning("Config Read Error at line " + path + ":");
                if (e instanceof NumberFormatException) {
                    Util.warning("Failed to read 'time:' variable in " + line);
                    Util.warning("Ensure you have a NUMBER after 'time:' specified");
                }
            }
        }

        return warns;
    }
}