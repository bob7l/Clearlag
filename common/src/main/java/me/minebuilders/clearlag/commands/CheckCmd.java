package me.minebuilders.clearlag.commands;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.RAMUtil;
import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.exceptions.WrongCommandArgumentException;
import me.minebuilders.clearlag.language.LanguageValue;
import me.minebuilders.clearlag.language.messages.MessageTree;
import me.minebuilders.clearlag.modules.CommandModule;
import me.minebuilders.clearlag.tasks.TPSTask;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Hopper;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CheckCmd extends CommandModule {

    @AutoWire
    private TPSTask tpsTask;

    @LanguageValue(key = "command.check.")
    private MessageTree lang;

    @Override
    protected void run(CommandSender sender, String[] args) throws WrongCommandArgumentException {
        final List<World> worlds;
        if (args.length > 0) {
            worlds = new ArrayList<>(args.length);
            for (String arg : args) {
                World world = Bukkit.getWorld(arg);
                if (world == null) {
                    throw new WrongCommandArgumentException(lang.getMessage("invalidworld"), arg);
                }

                worlds.add(world);
            }
        } else {
            worlds = Bukkit.getWorlds();
        }

        final AtomicInteger removed = new AtomicInteger(0);
        final AtomicInteger mobs = new AtomicInteger(0);
        final AtomicInteger animals = new AtomicInteger(0);
        final AtomicInteger chunks = new AtomicInteger(0);
        final AtomicInteger spawners = new AtomicInteger(0);
        final AtomicInteger activehoppers = new AtomicInteger(0);
        final AtomicInteger inactivehoppers = new AtomicInteger(0);
        final AtomicInteger players = new AtomicInteger(0);

        final List<Chunk> all = new ArrayList<>();
        for (World w : worlds) {
            Collections.addAll(all, w.getLoadedChunks());
        }

        if (all.isEmpty()) {
            lang.sendMessage("header", sender);
            lang.sendMessage("printed", sender,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    Util.getTime(System.currentTimeMillis() - ClearLag.getInstance().getInitialBootTimestamp()),
                    tpsTask.getStringTPS(),
                    RAMUtil.getUsedMemory(), RAMUtil.getMaxMemory(),
                    (RAMUtil.getMaxMemory() - RAMUtil.getUsedMemory())
            );
            lang.sendMessage("footer", sender);
            return;
        }

        final AtomicInteger pending = new AtomicInteger(all.size());

        for (Chunk c : all) {
            final World w = c.getWorld();
            final int cx = c.getX();
            final int cz = c.getZ();
            final int lx = (cx << 4) + 8;
            final int lz = (cz << 4) + 8;
            final int ly = w.getMinHeight() + 1;

            Location loc = new Location(w, lx, ly, lz);

            ClearLag.scheduler().runAtLocation(loc, task -> {
                try {
                    final Chunk rc = w.getChunkAt(cx, cz);

                    for (BlockState bt : rc.getTileEntities()) {
                        if (bt instanceof CreatureSpawner) {
                            spawners.incrementAndGet();
                        } else if (bt instanceof Hopper hop) {
                            if (!isHopperEmpty(hop)) {
                                activehoppers.incrementAndGet();
                            } else {
                                inactivehoppers.incrementAndGet();
                            }
                        }
                    }

                    for (Entity e : rc.getEntities()) {
                        switch (e) {
                            case Monster monster -> mobs.incrementAndGet();
                            case Player p -> players.incrementAndGet();
                            case Creature creature -> animals.incrementAndGet();
                            case Item item -> removed.incrementAndGet();
                            default -> {
                            }
                        }
                    }

                    chunks.incrementAndGet();
                } finally {
                    if (pending.decrementAndGet() == 0) {
                        if (sender instanceof Player plr) {
                            ClearLag.scheduler().runAtEntity(plr, t -> {
                                lang.sendMessage("header", plr);
                                lang.sendMessage("printed", plr,
                                        removed.get(),
                                        mobs.get(),
                                        animals.get(),
                                        players.get(),
                                        chunks.get(),
                                        activehoppers.get(),
                                        inactivehoppers.get(),
                                        spawners.get(),
                                        Util.getTime(System.currentTimeMillis() - ClearLag.getInstance().getInitialBootTimestamp()),
                                        tpsTask.getStringTPS(),
                                        RAMUtil.getUsedMemory(), RAMUtil.getMaxMemory(),
                                        (RAMUtil.getMaxMemory() - RAMUtil.getUsedMemory())
                                );
                                lang.sendMessage("footer", plr);
                            });
                        } else {
                            ClearLag.scheduler().runNextTick(t -> {
                                lang.sendMessage("header", sender);
                                lang.sendMessage("printed", sender,
                                        removed.get(),
                                        mobs.get(),
                                        animals.get(),
                                        players.get(),
                                        chunks.get(),
                                        activehoppers.get(),
                                        inactivehoppers.get(),
                                        spawners.get(),
                                        Util.getTime(System.currentTimeMillis() - ClearLag.getInstance().getInitialBootTimestamp()),
                                        tpsTask.getStringTPS(),
                                        RAMUtil.getUsedMemory(), RAMUtil.getMaxMemory(),
                                        (RAMUtil.getMaxMemory() - RAMUtil.getUsedMemory())
                                );
                                lang.sendMessage("footer", sender);
                            });
                        }
                    }
                }
            });
        }
    }

    private boolean isHopperEmpty(Hopper hop) {
        for (ItemStack it : hop.getInventory().getContents()) {
            if (it != null) return false;
        }

        return true;
    }
}
