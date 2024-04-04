package me.minebuilders.clearlag.modules;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.LinkedList;
import java.util.List;

public abstract class ClearModule extends ClearlagModule {

	public List<Entity> getRemovables(List<Entity> list, World w) {
		List<Entity> en = new LinkedList<>();
		
		if (isWorldEnabled(w)) {

			for (Entity ent : list) {
				if (isRemovable(ent)) {
					en.add(ent);
				}
			}

		}
       
		return en;
	}

	public List<Entity> getAllRemovables() {

		List<Entity> en = new LinkedList<>();

		for (World w : Bukkit.getWorlds()) {

			if (isWorldEnabled(w)) {

				for (Entity ent : w.getEntities()) {
					if (isRemovable(ent)) {
						en.add(ent);
					}
				}
			}
		}

		return en;
	}

	public abstract boolean isRemovable(Entity e);

	public boolean isWorldEnabled(World w) {
		return true;
	}

}
