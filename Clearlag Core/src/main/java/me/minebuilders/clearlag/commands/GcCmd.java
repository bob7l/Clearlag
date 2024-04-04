package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.modules.CommandModule;
import org.bukkit.command.CommandSender;

public class GcCmd extends CommandModule {

    @LanguageValue(key = "command.gc.message")
    private Message message;

    @Override
    protected void run(CommandSender sender, String[] args) {

        message.sendMessage(sender);

        System.gc();

    }

}