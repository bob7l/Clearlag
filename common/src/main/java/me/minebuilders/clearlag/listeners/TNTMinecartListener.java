package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleCreateEvent;

import java.util.concurrent.atomic.AtomicInteger;

@ConfigPath(path = "tnt-minecart")
public class TNTMinecartListener extends EventModule {

    @ConfigValue
    private int radius;

    @ConfigValue
    private int max;

    @EventHandler
    public void onVehicleCreate(VehicleCreateEvent event) {
        Entity mine = event.getVehicle();

        if (mine instanceof ExplosiveMinecart) {
            AtomicInteger count = new AtomicInteger(0);
            for (Entity tnt : mine.getNearbyEntities(radius, radius, radius)) {
                if (tnt instanceof ExplosiveMinecart) {
                    count.incrementAndGet();
                }
            }

            if (count.get() >= this.max) {
                ClearLag.scheduler().runAtEntity(mine, task -> mine.remove());
            }
        }
    }
}
