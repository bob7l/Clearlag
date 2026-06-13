package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityMap;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@ConfigPath(path = "per-entity-chunk-entity-limiter")
public class ChunkPerEntityLimiterListener extends EventModule {

    @ConfigValue(valueType = ConfigValueType.ENTITY_LIMIT_MAP)
    private EntityMap<Integer> entityLimits;

    private PassivePurger passivePurger = null;

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChunkLoad(ChunkLoadEvent event) {
        purgeEntities(event.getChunk().getEntities());
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        final Entity entity = event.getEntity();
        final Integer limit = entityLimits.getValue(entity);
        if (limit != null) {
            final Entity[] entities = event.getLocation().getChunk().getEntities();
            if (entities.length >= limit) {
                int count = 0;
                for (Entity e : entities)
                    if (entity.getType() == e.getType() && entityLimits.getValue(e).equals(limit)) {
                        ++count;
                    }

                event.setCancelled(count >= limit);
            }
        }
    }

    private void purgeEntities(final Entity[] entities) {
        if (entities.length > 0) {
            final EntityMap<Integer> entityMap = new EntityMap<>();
            for (Entity entity : entities) {
                final Integer limit = entityLimits.getValue(entity);
                if (limit != null) {
                    Integer amount = entityMap.getValue(entity);
                    if (amount == null) {
                        amount = 1;
                    } else {
                        ++amount;
                    }

                    entityMap.set(entity.getType(), amount);
                    if (amount > limit) {
                        ClearLag.scheduler().runAtEntity(entity, task -> entity.remove());
                    }
                }
            }
        }
    }

    private class PassivePurger {

        private final int chunkBatchSize;

        private int worldIndex = 0;
        private int chunkIndex = 0;

        private final AtomicBoolean cancelled = new AtomicBoolean(false);

        private PassivePurger(int chunkBatchSize) {
            this.chunkBatchSize = chunkBatchSize;
        }

        private void start() {
            ClearLag.scheduler().runTimer(task -> {
                if (cancelled.get()) {
                    task.cancel();
                    return;
                }

                final List<World> worlds = Bukkit.getWorlds();

                if (worlds.isEmpty()) {
                    return;
                }

                if (worldIndex >= worlds.size()) {
                    worldIndex = 0;
                }

                int processedChunks = 0;

                for (World world = worlds.get(worldIndex); processedChunks < chunkBatchSize;) {
                    final Chunk[] chunks = world.getLoadedChunks();
                    if (chunks.length > chunkIndex) {
                        for (; chunkIndex < chunks.length && processedChunks < chunkBatchSize; ++chunkIndex) {
                            purgeEntities(chunks[chunkIndex].getEntities());
                            ++processedChunks;
                        }
                    }

                    if (processedChunks < chunkBatchSize) {
                        if (++worldIndex >= worlds.size()) {
                            worldIndex = 0;
                            return;
                        }

                        chunkIndex = 0;
                    }
                }
            }, 1L, 1L);
        }

        private void cancel() {
            cancelled.set(true);
        }
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        if (ClearLag.getInstance().getConfig().getBoolean("per-entity-chunk-entity-limiter.passive-cleaner.passive-cleaning-enabled")) {
            int interval = Math.max(1, ClearLag.getInstance().getConfig().getInt("per-entity-chunk-entity-limiter.passive-cleaner.check-interval"));
            int batchSize = ClearLag.getInstance().getConfig().getInt("per-entity-chunk-entity-limiter.passive-cleaner.chunk-batch-size");
            passivePurger = new PassivePurger(batchSize);
            ClearLag.scheduler().runLater(task -> {
                if (passivePurger != null) {
                    passivePurger.start();
                }
            }, interval);
        }
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        if (passivePurger != null) {
            passivePurger.cancel();
            passivePurger = null;
        }
    }
}
