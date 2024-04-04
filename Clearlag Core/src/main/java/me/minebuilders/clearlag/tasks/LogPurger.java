package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Date;

@ConfigPath(path = "log-purger")
public class LogPurger extends TaskModule {

    @AutoWire
    private ConfigHandler configHandler;

    public void run() {

        long time = new Date().getTime() - (86400000L * configHandler.getConfig().getLong("log-purger.days-old"));

        File folder = new File(Bukkit.getServer().getWorldContainer().getAbsolutePath() + "/logs");

        if (!folder.exists()) {
            return;
        }

        File[] files = folder.listFiles();

        int deleted = 0;

        if (files != null) {

            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".log.gz") && time > Util.parseTime(file.getName().replace(".log.gz", "")).getTime()) {
                    file.delete();
                    deleted++;
                }
            }
        }

        Util.log(deleted + " Logs have been removed!");
    }

    @Override
    public int startTask() {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(Clearlag.getInstance(), this, 0L).getTaskId();
    }

}
