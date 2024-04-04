package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.removetype.CmdClear;
import org.bukkit.command.CommandSender;

public class ClearCmd extends CommandModule {

    @ConfigModule
    private final CmdClear cmdClear = new CmdClear();

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private ConfigHandler configHandler;

    @AutoWire
    private BroadcastHandler broadcastHandler;

    @LanguageValue(key = "command.clear.message")
    private Message clearMessage;

    @Override
    protected void run(CommandSender sender, String[] args) {

        final int i = entityManager.removeEntities(cmdClear);

        if (configHandler.getConfig().getBoolean("command-remove.broadcast-removal")) {

            broadcastHandler.broadcast(configHandler.getConfig().getString("auto-removal.broadcast-message").replace("+RemoveAmount", "" + i));

        } else {
            clearMessage.sendMessage(sender, i);
        }
    }

}
