package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ConfigPath(path = "spawn-limiter")
public class MobLimitListener extends EventModule implements Runnable {

    @ConfigValue
    private int animals;

    @ConfigValue
    private int mobs;

    @ConfigValue
    private int interval;

    private final AtomicBoolean cancelled = new AtomicBoolean(false);

    private volatile boolean canAnimalspawn = true;

    private volatile boolean canMobspawn = true;

    @Override
    public void run() {
        final List<World> worlds = Bukkit.getWorlds();
        if (worlds.isEmpty()) {
            canAnimalspawn = true;
            canMobspawn = true;
            return;
        }

        final AtomicInteger totalAnimals = new AtomicInteger(0);
        final AtomicInteger totalMobs = new AtomicInteger(0);
        final AtomicInteger pendingWorlds = new AtomicInteger(worlds.size());

        for (World world : worlds) {
            final Entity[] snapshot = world.getEntities().toArray(new Entity[0]);
            if (snapshot.length == 0) {
                if (pendingWorlds.decrementAndGet() == 0) {
                    canAnimalspawn = totalAnimals.get() < this.animals;
                    canMobspawn = totalMobs.get() < this.mobs;
                }

                continue;
            }

            final AtomicInteger animalsInWorld = new AtomicInteger(0);
            final AtomicInteger mobsInWorld = new AtomicInteger(0);
            final AtomicInteger pending = new AtomicInteger(snapshot.length);

            for (Entity e : snapshot) {
                ClearLag.scheduler().runAtEntity(e, task -> {
                    try {
                        if (e instanceof Animals || e instanceof Villager) {
                            animalsInWorld.incrementAndGet();
                        }
                        if (e instanceof Creature) {
                            mobsInWorld.incrementAndGet();
                        }
                    } finally {
                        if (pending.decrementAndGet() == 0) {
                            totalAnimals.addAndGet(animalsInWorld.get());
                            totalMobs.addAndGet(mobsInWorld.get());
                            if (pendingWorlds.decrementAndGet() == 0) {
                                canAnimalspawn = totalAnimals.get() < this.animals;
                                canMobspawn = totalMobs.get() < this.mobs;
                            }
                        }
                    }
                });
            }
        }
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        cancelled.set(false);
        ClearLag.scheduler().runTimer(task -> {
            if (cancelled.get()) {
                task.cancel();
                return;
            }

            run();
        }, interval * 20L, interval * 20L);
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        cancelled.set(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent e) {
        Entity en = e.getEntity();
        if (!canAnimalspawn && en instanceof Animals) {
            e.setCancelled(true);
        } else if (!canMobspawn && en instanceof Creature) {
            e.setCancelled(true);
        }
    }
}
