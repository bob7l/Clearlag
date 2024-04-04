package me.minebuilders.clearlag.listeners;

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

            int count = 0;

            for (Entity e : entities) {
                if (this.entities.containsEntity(e)) {

                    if (++count > limit) {
                        e.remove();
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

                int count = 0;

                for (Entity e : entities)
                    if (this.entities.containsEntity(e))
                        ++count;

                event.setCancelled(count >= limit);
            }
        }
    }
}
