package me.minebuilders.clearlag.triggeredremoval.cleanermodules;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.ClearlagModule;
import me.minebuilders.clearlag.triggeredremoval.triggers.CleanerTrigger;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * @author bob7l
 */
public class CommandExecuteJob extends ClearlagModule {

    @ConfigValue
    private List<String> commands;

    @ConfigValue
    private List<String> recoverCommands;

    private final CleanerTrigger cleanerTrigger;

    public CommandExecuteJob(CleanerTrigger cleanerTrigger) {
        this.cleanerTrigger = cleanerTrigger;
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        try {
            for (String s : commands)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        } catch (Exception e) {
            Util.warning("CommandExecuteJob was unable to dispatch commands!");
        }
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        try {
            for (String s : recoverCommands)
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s);
        } catch (Exception e) {
            Util.warning("CommandExecuteJob was unable to dispatch commands!");
        }
    }

    @Override
    public boolean isEnabled() {

        if (super.isEnabled() && !cleanerTrigger.shouldTrigger())
            setDisabled();

        return super.isEnabled();
    }
}
