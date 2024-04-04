package me.minebuilders.clearlag.removetype;

import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import me.minebuilders.clearlag.modules.ClearModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AreaClear extends ClearModule {

	@ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE, path = "area-filter")
	private EntityTable types;

	@Override
	public boolean isRemovable(Entity e) {
		return (!(e instanceof Player) && !types.containsEntity(e));
	}

}