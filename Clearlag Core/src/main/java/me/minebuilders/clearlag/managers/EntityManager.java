package me.minebuilders.clearlag.managers;

import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.events.EntityRemoveEvent;
import me.minebuilders.clearlag.modules.ClearModule;
import me.minebuilders.clearlag.modules.ClearlagModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public class EntityManager extends ClearlagModule {

    @ConfigValue(path = "settings.enable-api")
	private final boolean enabled = true;

	public int removeEntities(ClearModule mod) {
		int removed = 0;

		for (World w : Bukkit.getWorlds()) {
			removed += removeEntities(mod.getRemovables(w.getEntities(), w), w);
		}

		return removed;
	}

	public int removeEntities(List<Entity> removables, World w) {

		EntityRemoveEvent et = new EntityRemoveEvent(removables, w);

		if (enabled)
			Bukkit.getPluginManager().callEvent(et);

		for (Entity en : et.getEntityList())
			en.remove();

		return et.getEntityList().size();
	}
}