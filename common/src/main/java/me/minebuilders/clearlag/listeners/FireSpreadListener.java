package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;

@ConfigPath(path = "firespread-reducer")
public class FireSpreadListener extends EventModule {

    private long nextAllowedSpread = System.currentTimeMillis();

    @ConfigValue
    private int time;

    @EventHandler
    public void fireSpread(BlockIgniteEvent event) {
        if (event.getCause() == IgniteCause.SPREAD) {
            if (System.currentTimeMillis() > nextAllowedSpread) {
                nextAllowedSpread = (System.currentTimeMillis() + time);
            } else {
                event.setCancelled(true);
            }
        }
    }
}
