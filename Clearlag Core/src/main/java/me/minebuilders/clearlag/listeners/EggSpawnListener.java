package me.minebuilders.clearlag.listeners;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

@ConfigPath(path = "mobegg-limiter")
public class EggSpawnListener extends EventModule {

	@ConfigValue
	private int maxMobs;

	@ConfigValue
	private int checkRadius;

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onCreatureSpawn(CreatureSpawnEvent event) {

		if (event.getSpawnReason() == SpawnReason.SPAWNER_EGG) {

			final Entity e = event.getEntity();

			if (countNearbyLivingEntities(e, checkRadius) > maxMobs) {
				event.setCancelled(true);
			}
		}
	}

	private int countNearbyLivingEntities(Entity checkEntity, int checkRadius) {

		int count = 0;

		//Use a loop because in some CraftBukkit's, this returns an array rather then a list
		for (Entity ignored : checkEntity.getNearbyEntities(checkRadius, checkRadius, checkRadius))
			++count;

		return count;
	}
}
