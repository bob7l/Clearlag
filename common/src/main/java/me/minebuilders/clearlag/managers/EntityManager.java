package me.minebuilders.clearlag.managers;

import me.minebuilders.clearlag.ClearLag;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.events.EntityRemoveEvent;
import me.minebuilders.clearlag.modules.ClearModule;
import me.minebuilders.clearlag.modules.ClearlagModule;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EntityManager extends ClearlagModule {

    @ConfigValue(path = "settings.enable-api")
	private final boolean enabled = true;

	public int removeEntities(ClearModule mod) {
		AtomicInteger removed = new AtomicInteger(0);
		for (World w : Bukkit.getWorlds()) {
			removed.addAndGet(removeEntities(mod.getRemovables(w.getEntities(), w), w));
		}

		return removed.get();
	}

	public int removeEntities(List<Entity> removables, World w) {

		EntityRemoveEvent et = new EntityRemoveEvent(removables, w);
		if (enabled) {
            Bukkit.getPluginManager().callEvent(et);
        }

		for (Entity en : et.getEntityList()) {
            ClearLag.scheduler().runAtEntity(en, task -> en.remove());
        }

		return et.getEntityList().size();
	}
}
