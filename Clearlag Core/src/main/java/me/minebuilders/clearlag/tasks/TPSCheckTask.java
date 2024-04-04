package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.modules.TaskModule;
import org.bukkit.Bukkit;

import java.util.List;

@ConfigPath(path = "tps-meter")
public class TPSCheckTask extends TaskModule {

    @ConfigValue
    private List<String> commands;

    @ConfigValue
    private List<String> recoverCommands;

    @ConfigValue
    private double tpsTrigger;

    @ConfigValue
    private double tpsRecover;

    @ConfigValue(valueType = ConfigValueType.COLORED_STRING)
    private String triggerBroadcastMessage;

    @ConfigValue(valueType = ConfigValueType.COLORED_STRING)
    private String recoverBroadcastMessage;

    @ConfigValue
    private boolean broadcastEnabled;

    @AutoWire
    private TPSTask tpsTask;

    @AutoWire
    private ConfigHandler configHandler;

    private boolean isRecovered = true;

    public void run() {
        double tps = tpsTask.getTPS();

        if (tps <= tpsTrigger && isRecovered) {

            if (broadcastEnabled)
                Bukkit.broadcastMessage(triggerBroadcastMessage);

            try {
                for (String s : commands)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            } catch (Exception e) {
                Util.warning("TPSCheckTask was unable to dispatch commands!");
            } finally {
                isRecovered = false;
            }
        } else if (tps >= tpsRecover && !isRecovered) {
            try {

                if (broadcastEnabled)
                    Bukkit.broadcastMessage(recoverBroadcastMessage);

                for (String s : recoverCommands)
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
            } catch (Exception e) {
                Util.warning("TPSCheckTask was unable to dispatch commands!");
            } finally {
                isRecovered = true;
            }
        }
    }

    @Override
    public int getInterval() {
        return configHandler.getConfig().getInt("tps-meter.interval") * 20;
    }

    @Override
    protected int startTask() {
        return Bukkit.getScheduler().scheduleSyncRepeatingTask(Clearlag.getInstance(), this, 420, getInterval());
    }

}
