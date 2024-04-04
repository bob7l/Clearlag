package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkLoadEvent;

@ConfigPath(path = "chunk-limiter")
public class ChunkLimiterListener extends EventModule {

    @ConfigValue
    private int limit;

    @ConfigValue
    private boolean createNewChunks;

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if ((!createNewChunks && event.isNewChunk()) || (countChunks() >= limit)) {
            event.getChunk().unload(false);

            for (Chunk c : event.getWorld().getLoadedChunks()) {
                c.unload(true);
            }
        }
    }

    private int countChunks() {

        int size = 0;

        for (World w : Bukkit.getWorlds()) {
            size += w.getLoadedChunks().length;
        }

        return size;
    }
}
