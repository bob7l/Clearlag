package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleCreateEvent;

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
            int max = 0;

            for (Entity tnt : mine.getNearbyEntities(radius, radius, radius)) {
                if (tnt instanceof ExplosiveMinecart) {
                    max++;
                }
            }

            if (max >= this.max) {
                mine.remove();
            }
        }
    }
}
