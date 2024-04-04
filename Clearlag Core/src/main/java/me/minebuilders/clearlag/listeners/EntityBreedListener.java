package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@ConfigPath(path = "mob-breeding-limiter")
public class EntityBreedListener extends EventModule {

	@ConfigValue
	private int maxMobs;

	@ConfigValue
	private int checkRadius;

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		if (event.getSpawnReason() == SpawnReason.EGG || event.getSpawnReason() == SpawnReason.BREEDING) {

			final Entity eventEntity = event.getEntity();

			int count = 0;

			for (Entity entity : eventEntity.getNearbyEntities(checkRadius, checkRadius, checkRadius)) {

				if (entity instanceof Ageable) {

					if (++count >= maxMobs) {

						event.setCancelled(true);

						return;
					}
				}
			}
		}
	}
}
