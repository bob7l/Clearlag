package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.removetype.AreaClear;
import me.minebuilders.clearlag.ClearLag;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

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

            final World world = player.getWorld();
            final double px = player.getLocation().getX();
            final double py = player.getLocation().getY();
            final double pz = player.getLocation().getZ();
            final double r2 = (double) radius * (double) radius;

            ClearLag.scheduler().runNextTick(task -> {
                final List<Entity> nearby = new ArrayList<>();

                for (Entity e : world.getEntities()) {
                    if (e.getWorld() != world) continue;

                    final double dx = e.getLocation().getX() - px;
                    final double dy = e.getLocation().getY() - py;
                    final double dz = e.getLocation().getZ() - pz;

                    if ((dx * dx + dy * dy + dz * dz) <= r2) {
                        nearby.add(e);
                    }
                }

                final int removed = entityManager.removeEntities(areaClear.getRemovables(nearby, world), world);

                ClearLag.scheduler().runAtEntity(player, nextTask -> lang.sendMessage("message", player, removed, radius));
            });

        } catch (NumberFormatException e) {
            throw new WrongCommandArgumentException(lang.getMessage("error"));
        }
    }
}
