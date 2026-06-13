package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.adapters.VersionAdapter;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.config.configvalues.Warning;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import me.minebuilders.clearlag.removetype.AutoClear;
import me.minebuilders.clearlag.ClearLag;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@ConfigPath(path = "auto-removal")
public class ClearTask extends TaskModule {

    @ConfigModule
    private final AutoClear autoClear = new AutoClear();

    @ConfigValue
    private int autoremovalInterval;

    @ConfigValue(valueType = ConfigValueType.COLORED_STRINGS)
    private String[] broadcastMessage;

    @ConfigValue(valueType = ConfigValueType.WARN_ARRAY)
    private HashMap<Integer, Warning> warnings;

    @ConfigValue(valueType = ConfigValueType.PRIMITIVE)
    private boolean broadcastRemoval;

    @ConfigValue(valueType = ConfigValueType.PRIMITIVE)
    private String broadcastSound;

    @ConfigValue(valueType = ConfigValueType.PRIMITIVE)
    private boolean actionbar;

    @AutoWire
    private VersionAdapter versionAdapter;

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private BroadcastHandler broadcastHandler;

    private int interval = 0;

    public void run() {

        ++interval;

        final Warning warning = warnings != null ? warnings.get(interval) : null;

        if (warning != null) {
            final String[] formatted = Util.cloneAndReplaceStringArr(warning.getMessages(), "+remaining", "" + (autoremovalInterval - interval));
            broadcastHandler.broadcast(formatted);

            if (actionbar) {
                sendActionBar(formatted);
            }

            if (warning.hasSound()) {
                playSound(warning.getSound());
            }
        }

        if (interval >= autoremovalInterval) {
            final List<World> worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                if (broadcastRemoval) {
                    broadcastRemovalMessage(0);
                }

                interval = 0;
                return;
            }

            final AtomicInteger totalPendingWorlds = new AtomicInteger(worlds.size());
            final AtomicInteger totalRemoved = new AtomicInteger(0);

            for (World w : worlds) {
                final Entity[] snapshot = w.getEntities().toArray(new Entity[0]);

                if (snapshot.length == 0) {
                    if (totalPendingWorlds.decrementAndGet() == 0) {
                        if (broadcastRemoval) {
                            broadcastRemovalMessage(totalRemoved.get());
                        }
                    }
                    continue;
                }

                final AtomicInteger pending = new AtomicInteger(snapshot.length);
                final ConcurrentLinkedQueue<Entity> matched = new ConcurrentLinkedQueue<>();

                for (Entity e : snapshot) {
                    ClearLag.scheduler().runAtEntity(e, task -> {
                        try {
                            if (autoClear.isWorldEnabled(w) && autoClear.isRemovable(e)) {
                                matched.add(e);
                            }
                        } finally {
                            if (pending.decrementAndGet() == 0) {
                                int removed = entityManager.removeEntities(new ArrayList<>(matched), w);
                                totalRemoved.addAndGet(removed);

                                if (totalPendingWorlds.decrementAndGet() == 0) {
                                    if (broadcastRemoval) {
                                        broadcastRemovalMessage(totalRemoved.get());
                                    }
                                }
                            }
                        }
                    });
                }
            }

            interval = 0;
        }
    }

    private void broadcastRemovalMessage(int removedAmount) {
        final String[] formatted = Util.cloneAndReplaceStringArr(broadcastMessage, "+RemoveAmount", String.valueOf(removedAmount));
        broadcastHandler.broadcast(formatted);

        if (actionbar) {
            sendActionBar(formatted);
        }

        if (broadcastSound != null && !broadcastSound.isEmpty()) {
            try {
                playSound(org.bukkit.Sound.valueOf(broadcastSound.toUpperCase()));
            } catch (IllegalArgumentException ex) {
                Util.warning("Invalid broadcast-sound '" + broadcastSound + "' - use a valid Sound name for your version.");
            }
        }
    }

    private void sendActionBar(String[] lines) {
        if (versionAdapter == null) {
            return;
        }

        for (String line : lines) {
            final String colored = Util.color(line);
            Util.postToMainThread(() -> {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    versionAdapter.sendActionBar(p, colored);
                }
            });
        }
    }

    private void playSound(org.bukkit.Sound sound) {
        Util.postToMainThread(() -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.playSound(p.getLocation(), sound, 1.0f, 1.0f);
            }
        });
    }
}