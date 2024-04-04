package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.*;

@ConfigPath(path = "live-time")
public class LiveTask extends TaskModule {

    @ConfigValue
    private int itemlivetime;

    @ConfigValue
    private int moblivetime;

    @ConfigValue
    private int arrowkilltime;

    @ConfigValue
    private boolean itemtimer;

    @ConfigValue
    private boolean mobtimer;

    @ConfigValue
    private boolean arrowtimer;

    @AutoWire
    private ConfigHandler configHandler;

    public void run() {
        for (World w : Bukkit.getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (mobtimer && e instanceof LivingEntity && !(e instanceof HumanEntity)) {
                    if (e.getTicksLived() > moblivetime) e.remove();
                } else if (itemtimer && e instanceof Item) {
                    if (e.getTicksLived() > itemlivetime) e.remove();
                } else if (arrowtimer && e instanceof Arrow) {
                    if (e.getTicksLived() > arrowkilltime) e.remove();
                }
            }
        }
    }

    @Override
    public int getInterval() {
        return configHandler.getConfig().getInt("live-time.interval") * 20;
    }

}
