package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.tasks.HaltTask;
import org.bukkit.command.CommandSender;

public class HaltCmd extends CommandModule {

    @AutoWire
    private HaltTask halttask;

    @LanguageValue(key = "command.halt.")
    private MessageTree lang;

    @Override
    protected void run(CommandSender sender, String[] args) {

        if (!halttask.isEnabled() && (args.length <= 0 || args[0].equalsIgnoreCase("on"))) {

            halttask.setEnabled();

            lang.sendMessage("halted", sender);

        } else if (halttask.isEnabled() && (args.length <= 0 || args[0].equalsIgnoreCase("off"))) {

            halttask.setDisabled();

            lang.sendMessage("unhalted", sender);

        } else {
            wrongUsage.sendMessage(sender, usage, displayName);
        }
    }
}
