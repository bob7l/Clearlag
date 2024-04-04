package me.minebuilders.clearlag.triggeredremoval;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.modules.ClearlagModule;
import me.minebuilders.clearlag.triggeredremoval.cleanermodules.CommandExecuteJob;
import me.minebuilders.clearlag.triggeredremoval.cleanermodules.EntityCleanerJob;
import me.minebuilders.clearlag.triggeredremoval.cleanermodules.WarningJob;
import me.minebuilders.clearlag.triggeredremoval.triggers.CleanerTrigger;
import me.minebuilders.clearlag.triggeredremoval.triggers.EntityLimitTrigger;
import me.minebuilders.clearlag.triggeredremoval.triggers.TPSTrigger;
import org.bukkit.configuration.Configuration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bob7l
 * <p>
 * Handles automatic triggers to entity removal.
 */
@ConfigPath(path = "custom-trigger-removal")
public class TriggerManager extends ClearlagModule {

    @AutoWire
    private ConfigHandler configHandler;

    private final Map<CleanerTrigger, BukkitTask> tirggerTaskMap = new HashMap<>(2);

    @Override
    public void setEnabled() {

        final Configuration config = configHandler.getConfig();

        if (config.getConfigurationSection("custom-trigger-removal.triggers") == null) {
            enabled = false;

            Util.warning("custom-trigger-removal is enabled, yet doeesn't contain any triggers? Disabling...");

            return;
        }

        super.setEnabled();

        for (String triggerKey : config.getConfigurationSection("custom-trigger-removal.triggers").getKeys(false)) {

            final CleanerTrigger trigger;

            CleanerHandler cleanerHandler = new CleanerHandler();

            final String cleanerType = config.getString("custom-trigger-removal.triggers." + triggerKey + ".trigger-type");

            if (cleanerType != null) {

                if (cleanerType.equalsIgnoreCase("tps-trigger")) {
                    trigger = new TPSTrigger(cleanerHandler);
                } else if (cleanerType.equalsIgnoreCase("entity-limit-trigger")) {
                    trigger = new EntityLimitTrigger(cleanerHandler);
                } else
                    trigger = null;

            } else {
                Util.warning("You must specify a trigger-type for trigger " + triggerKey);
                continue;
            }

            if (trigger == null) {
                Util.warning("Unknown trigger specified: " + triggerKey);
                Util.warning("Trigger " + triggerKey + " will be ignored!");
            } else {

                try {
                    configHandler.setObjectConfigValues(trigger, "custom-trigger-removal.triggers." + triggerKey);
                    Clearlag.getInstance().getAutoWirer().wireObject(trigger);
                } catch (Exception e) {
                    Util.warning("Failed to set config variables for trigger '" + triggerKey + "'");
                    e.printStackTrace();
                    continue;
                }

                for (String cleanerKey : config.getConfigurationSection("custom-trigger-removal.triggers." + triggerKey + ".jobs").getKeys(false)) {

                    ClearlagModule module = null;

                    if (cleanerKey.equalsIgnoreCase("entity-clearer")) {
                        module = new EntityCleanerJob();
                    } else if (cleanerKey.equalsIgnoreCase("command-executor")) {
                        module = new CommandExecuteJob(trigger);
                    }

                    if (module == null) {
                        Util.warning("Unknown job specified: " + cleanerKey);
                        Util.warning("Job " + cleanerKey + " will be ignored!");
                    } else {

                        try {
                            configHandler.setObjectConfigValues(module, "custom-trigger-removal.triggers." + triggerKey + ".jobs." + cleanerKey);
                            Clearlag.getInstance().getAutoWirer().wireObject(module);
                        } catch (Exception e) {
                            Util.warning("Failed to set config variables for job '" + cleanerKey + "', and trigger " + triggerKey);
                            e.printStackTrace();
                            continue;
                        }

                        if (config.get("custom-trigger-removal.triggers." + triggerKey + ".jobs." + cleanerKey + ".warnings") != null) {
                            module = new WarningJob(module);

                            try {
                                configHandler.setObjectConfigValues(module, "custom-trigger-removal.triggers." + triggerKey + ".jobs." + cleanerKey);
                                Clearlag.getInstance().getAutoWirer().wireObject(module);
                            } catch (Exception e) {
                                Util.warning("Failed to set config variables for warnings on job '" + cleanerKey + "', and trigger " + triggerKey);
                                e.printStackTrace();
                                continue;
                            }
                        }

                        cleanerHandler.addCleanerJob(module);
                    }
                }

                final BukkitTask runnableTask = new BukkitRunnable() {

                    @Override
                    public void run() {
                        trigger.runTrigger();
                    }

                }.runTaskTimer(Clearlag.getInstance(), trigger.getCheckFrequency(), trigger.getCheckFrequency());

                tirggerTaskMap.put(trigger, runnableTask);
            }
        }
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        for (BukkitTask task : tirggerTaskMap.values())
            task.cancel();

        tirggerTaskMap.clear();
    }
}
