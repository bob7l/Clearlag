package me.minebuilders.clearlag.removetype;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import org.bukkit.entity.Entity;

@ConfigPath(path = "auto-removal")
public class AutoClear extends LimitClear {

    @ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE)
    private EntityTable removeEntities;

    @Override
    public boolean isRemovable(Entity e) {

        if (removeEntities.containsEntity(e))
            return true;

        return super.isRemovable(e);
    }

}