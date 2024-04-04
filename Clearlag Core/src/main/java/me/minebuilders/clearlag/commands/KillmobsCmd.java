package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.removetype.KillMobsClear;
import org.bukkit.command.CommandSender;

public class KillmobsCmd extends CommandModule {

    @ConfigModule
    private final KillMobsClear killMobsClear = new KillMobsClear();

    @AutoWire
    private EntityManager entityManager;

    @LanguageValue(key = "command.killmobs.message")
    private Message message;

    @Override
    protected void run(CommandSender sender, String[] args) {
        message.sendMessage(sender, entityManager.removeEntities(killMobsClear));
    }

}
