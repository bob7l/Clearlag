package me.minebuilders.clearlag.triggeredremoval;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import me.minebuilders.clearlag.ClearLag;
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

    private final Map<CleanerTrigger, WrappedTask> triggerTaskMap = new HashMap<>(2);

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
                } else {
                    trigger = null;
                }
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
                    ClearLag.getInstance().getAutoWirer().wireObject(trigger);
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
                            ClearLag.getInstance().getAutoWirer().wireObject(module);
                        } catch (Exception e) {
                            Util.warning("Failed to set config variables for job '" + cleanerKey + "', and trigger " + triggerKey);
                            e.printStackTrace();
                            continue;
                        }

                        if (config.get("custom-trigger-removal.triggers." + triggerKey + ".jobs." + cleanerKey + ".warnings") != null) {
                            module = new WarningJob(module);
                            try {
                                configHandler.setObjectConfigValues(module, "custom-trigger-removal.triggers." + triggerKey + ".jobs." + cleanerKey);
                                ClearLag.getInstance().getAutoWirer().wireObject(module);
                            } catch (Exception e) {
                                Util.warning("Failed to set config variables for warnings on job '" + cleanerKey + "', and trigger " + triggerKey);
                                e.printStackTrace();
                                continue;
                            }
                        }

                        cleanerHandler.addCleanerJob(module);
                    }
                }

                final WrappedTask runnableTask = ClearLag.scheduler().runTimer(trigger::runTrigger, trigger.getCheckFrequency(), trigger.getCheckFrequency());
                triggerTaskMap.put(trigger, runnableTask);
            }
        }
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        for (WrappedTask task : triggerTaskMap.values()) {
            ClearLag.scheduler().cancelTask(task);
        }

        triggerTaskMap.clear();
    }
}
