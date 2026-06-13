package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.ChunkUnloadEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

@ConfigPath(path = "mobspawner")
public class MobSpawerListener extends EventModule {

    @ConfigValue
    private int maxSpawn;

    @ConfigValue
    private boolean removeMobsOnChunkUnload;

    private final ConcurrentHashMap<ChunkKey, ConcurrentLinkedQueue<Entity>> map = new ConcurrentHashMap<>();

    private boolean isOverLimit(ConcurrentLinkedQueue<Entity> en) {
        en.removeIf(Entity::isDead);
        int amount = 0;
        for (Entity e : en) {
            if (!e.isDead()) {
                amount++;
                if (amount >= maxSpawn) {
                    return true;
                }
            }
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

        final LivingEntity e = event.getEntity();
        if (event.getSpawnReason() == SpawnReason.SPAWNER) {
            ChunkKey key = new ChunkKey(event.getLocation().getChunk());
            ConcurrentLinkedQueue<Entity> entities = map.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>());
            if (removeMobsOnChunkUnload) {
                e.setRemoveWhenFarAway(true);
            }

            if (!isOverLimit(entities)) {
                entities.add(e);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onChunkUnload(ChunkUnloadEvent event) {
        map.remove(new ChunkKey(event.getChunk()));
    }

    private static class ChunkKey {

        private final int x;
        private final int z;

        private final UUID worldUuid;

        ChunkKey(Chunk c) {
            this.x = c.getX();
            this.z = c.getZ();
            this.worldUuid = c.getWorld().getUID();
        }

        @Override
        public int hashCode() {
            return (x * 33) + (z * 67) + (worldUuid.hashCode() / 4);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }

            ChunkKey ck = (ChunkKey) obj;

            return (ck.x == x && ck.z == z && ck.worldUuid == worldUuid);
        }
    }
}
