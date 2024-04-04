package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.RAMUtil;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;

import java.util.List;

@ConfigPath(path = "ram-meter")
public class RAMCheckTask extends TaskModule {

    @ConfigValue
    private List<String> commands;

    @ConfigValue
    private int ramLimit;

    @AutoWire
    private ConfigHandler configHandler;

    public void run() {

        if (RAMUtil.getUsedMemory() > ramLimit) {
            try {
                for (String s : commands)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            } catch (Exception e) {
                Util.warning("RAMCheckTask was unable to dispatch commands!");
            }
        }
    }

    @Override
    public int getInterval() {
        return configHandler.getConfig().getInt("ram-meter.interval") * 20;
    }

}
