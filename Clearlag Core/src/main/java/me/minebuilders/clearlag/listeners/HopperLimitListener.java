package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ChunkKey;
import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Bukkit;
import org.bukkit.block.Hopper;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ConfigPath(path = "hopper-limiter")
public class HopperLimitListener extends EventModule implements Runnable {

    @ConfigValue
    private final int transferLimit = 6;

    @ConfigValue
    private final int checkInterval = 1;

    private int schedulerID = -1;

    private Map<ChunkKey, Integer> hopperDataMap = new HashMap<>();

    @EventHandler
    public void onHopper(InventoryMoveItemEvent event) {

        if (event.getSource().getHolder() instanceof Hopper) {

            ChunkKey chunkKey = new ChunkKey(((Hopper) event.getSource().getHolder()).getChunk());

            Integer transfers = hopperDataMap.get(chunkKey);

            if (transfers == null) {
                transfers = 0;
            }

            if (transfers >= transferLimit)
                event.setCancelled(true);
            else
                ++transfers;

            hopperDataMap.put(chunkKey, transfers);
        }
    }


    @Override
    public void run() {

        Iterator<Map.Entry<ChunkKey, Integer>> entries = hopperDataMap.entrySet().iterator();

        while (entries.hasNext()) {

            Map.Entry<ChunkKey, Integer> entry = entries.next();

            if (entry.getValue() == 0) {
                entries.remove();
            } else {
                entry.setValue(0);
            }
        }
    }

    @Override
    public void setEnabled() {
        super.setEnabled();

        schedulerID = Bukkit.getScheduler().scheduleSyncRepeatingTask(Clearlag.getInstance(), this, checkInterval * 20L, checkInterval * 20L);
    }

    @Override
    public void setDisabled() {
        super.setDisabled();

        if (schedulerID != -1) {
            Bukkit.getServer().getScheduler().cancelTask(schedulerID);
            hopperDataMap = new HashMap<>();
        }
    }
}
