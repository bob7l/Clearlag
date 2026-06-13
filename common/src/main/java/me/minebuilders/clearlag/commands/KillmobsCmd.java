package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.config.ConfigHandler;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.Message;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.removetype.KillMobsClear;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class KillmobsCmd extends CommandModule {

    @ConfigModule
    private final KillMobsClear killMobsClear = new KillMobsClear();

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private ConfigHandler configHandler;

    @LanguageValue(key = "command.killmobs.message")
    private Message killMobsMessage;

    @Override
    protected void run(CommandSender sender, String[] args) {
        final List<World> worlds = Bukkit.getWorlds();
        final AtomicInteger pendingWorlds = new AtomicInteger(worlds.size());
        final AtomicInteger totalRemoved = new AtomicInteger(0);

        if (worlds.isEmpty()) {
            killMobsMessage.sendMessage(sender, 0);
            return;
        }

        for (World w : worlds) {
            final Entity[] snapshot = w.getEntities().toArray(new Entity[0]);

            if (snapshot.length == 0) {
                if (pendingWorlds.decrementAndGet() == 0) {
                    killMobsMessage.sendMessage(sender, totalRemoved.get());
                }
                continue;
            }

            final ConcurrentLinkedQueue<Entity> matched = new ConcurrentLinkedQueue<>();
            final AtomicInteger pending = new AtomicInteger(snapshot.length);

            for (Entity en : snapshot) {
                ClearLag.scheduler().runAtEntity(en, task -> {
                    try {
                        if (killMobsClear.isWorldEnabled(w) && killMobsClear.isRemovable(en)) {
                            matched.add(en);
                        }
                    } finally {
                        if (pending.decrementAndGet() == 0) {
                            final int removed = entityManager.removeEntities(new ArrayList<>(matched), w);
                            totalRemoved.addAndGet(removed);

                            if (pendingWorlds.decrementAndGet() == 0) {
                                if (sender instanceof Player plr) {
                                    ClearLag.scheduler().runAtEntity(plr, nextTask -> killMobsMessage.sendMessage(plr, totalRemoved.get()));
                                } else {
                                    ClearLag.scheduler().runNextTick(additionalTask -> killMobsMessage.sendMessage(sender, totalRemoved.get()));
                                }
                            }
                        }
                    }
                });
            }
        }
    }
}
