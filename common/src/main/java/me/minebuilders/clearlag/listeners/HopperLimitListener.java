package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ChunkKey;
import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@ConfigPath(path = "hopper-limiter")
public class HopperLimitListener extends EventModule {

    @ConfigValue
    private final int transferLimit = 6;

    @ConfigValue
    private final int checkInterval = 1;

    private final ConcurrentHashMap<ChunkKey, AtomicInteger> hopperDataMap = new ConcurrentHashMap<>();
    private final AtomicBoolean running = new AtomicBoolean(false);

    @EventHandler
    public void onHopper(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() instanceof Hopper hopper) {
            final ChunkKey key = new ChunkKey(hopper.getChunk());
            final AtomicInteger count = hopperDataMap.computeIfAbsent(key, k -> new AtomicInteger());
            if (count.incrementAndGet() > transferLimit) {
                event.setCancelled(true);
            }
        }
    }

    private void resetCounters() {
        hopperDataMap.entrySet().removeIf(e -> e.getValue().get() == 0);
        hopperDataMap.replaceAll((k, v) -> new AtomicInteger());
    }

    @Override
    public void setEnabled() {
        super.setEnabled();
        running.set(true);

        ClearLag.scheduler().runTimer(task -> {
            if (!running.get()) {
                task.cancel();
                return;
            }

            resetCounters();
        }, checkInterval * 20L, checkInterval * 20L);
    }

    @Override
    public void setDisabled() {
        super.setDisabled();
        running.set(false);
        hopperDataMap.clear();
    }
}
