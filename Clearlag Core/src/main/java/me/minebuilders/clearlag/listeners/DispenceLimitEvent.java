package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDispenseEvent;

@ConfigPath(path = "dispenser-reducer")
public class DispenceLimitEvent extends EventModule {

    private long nextAllowedDispense;

    @ConfigValue
    private long time;

    @EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent e) {

        if (System.currentTimeMillis() > nextAllowedDispense) {
            nextAllowedDispense = (System.currentTimeMillis() + time);
        } else {
            e.setCancelled(true);
        }
    }

}