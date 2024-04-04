package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.removetype.AreaClear;
import org.bukkit.entity.Player;

public class AreaCmd extends CommandModule {

    @ConfigModule
    private final AreaClear areaClear = new AreaClear();

    @AutoWire
    private EntityManager entityManager;

    @LanguageValue(key = "command.area.")
    private MessageTree lang;

    public AreaCmd() {
        argLength = 1;
    }

    @Override
    protected void run(Player player, String[] args) throws WrongCommandArgumentException {

        try {

            final int radius = Integer.parseInt(args[0]);

            final int removed = entityManager.removeEntities(areaClear.getRemovables(player.getNearbyEntities(radius, radius, radius), player.getWorld()), player.getWorld());

            lang.sendMessage("message", player, removed, radius);

        } catch (NumberFormatException e) {
            throw new WrongCommandArgumentException(lang.getMessage("error"));
        }
    }

}
