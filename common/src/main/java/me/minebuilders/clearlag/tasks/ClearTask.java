package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Util;
import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigModule;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.BroadcastHandler;
import me.minebuilders.clearlag.modules.TaskModule;
import me.minebuilders.clearlag.removetype.AutoClear;
import me.minebuilders.clearlag.ClearLag;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

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
    private HashMap<Integer, String[]> warnings;

    @ConfigValue(valueType = ConfigValueType.PRIMITIVE)
    private boolean broadcastRemoval;

    @AutoWire
    private EntityManager entityManager;

    @AutoWire
    private BroadcastHandler broadcastHandler;

    private int interval = 0;

    public void run() {

        final String[] broadcastWarning = warnings != null ? warnings.get(++interval) : null;

        if (broadcastWarning != null) {
            broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastWarning, "+remaining", "" + (autoremovalInterval - interval)));
        }

        if (interval >= autoremovalInterval) {
            final List<World> worlds = Bukkit.getWorlds();
            if (worlds.isEmpty()) {
                if (broadcastRemoval) {
                    broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastMessage, "+RemoveAmount", "0"));
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
                            broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastMessage, "+RemoveAmount", String.valueOf(totalRemoved.get())));
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
                                        broadcastHandler.broadcast(Util.cloneAndReplaceStringArr(broadcastMessage, "+RemoveAmount", String.valueOf(totalRemoved.get())));
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
}
