package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.concurrent.atomic.AtomicInteger;

@ConfigPath(path = "chunk-entity-limiter")
public class ChunkEntityLimiterListener extends EventModule {

    @ConfigValue
    private int limit;

    @ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE)
    private EntityTable entities;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        Entity[] entities = event.getChunk().getEntities();
        if (entities.length >= limit) {
            AtomicInteger count = new AtomicInteger(0);
            for (Entity e : entities) {
                if (this.entities.containsEntity(e)) {
                    if (count.incrementAndGet() > limit) {
                        ClearLag.scheduler().runAtEntity(e, task -> e.remove());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        if (this.entities.containsEntity(event.getEntity())) {
            Entity[] entities = event.getLocation().getChunk().getEntities();
            if (entities.length >= limit) {
                AtomicInteger count = new AtomicInteger(0);
                for (Entity e : entities) {
                    if (this.entities.containsEntity(e)) {
                        count.incrementAndGet();
                    }
                }

                event.setCancelled(count.get() >= limit);
            }
        }
    }
}
