package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.tasks.TPSTask;
import org.bukkit.command.CommandSender;

public class TpsCmd extends CommandModule {

    @AutoWire
    private TPSTask tpsTask;

    @LanguageValue(key = "command.tps.print")
    private Message tpsPrintMessage;

    @Override
    protected void run(CommandSender sender, String[] args) {
        tpsPrintMessage.sendMessage(sender, tpsTask.getStringTPS());
    }

}
