package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

@ConfigPath(path = "player-speed-limiter")
public class ChunkOverloadListener extends EventModule {

    @ConfigValue
    private double moveMaxSpeed;

    @ConfigValue
    private double flyMaxSpeed;


    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {

        if (!event.getPlayer().isInsideVehicle()) {

            final Location to = event.getTo();
            final Location from = event.getFrom();

            if (to.getBlockX() != from.getBlockX() || to.getBlockZ() != from.getBlockZ()) {

                double distance = Math.hypot(from.getX() - to.getX(), from.getZ() - to.getZ());

                if (distance > (event.getPlayer().isFlying() ? flyMaxSpeed : moveMaxSpeed))
                    event.setTo(from);
            }
        }
    }
}
