package me.minebuilders.clearlag.removetype;

import me.minebuilders.clearlag.annotations.ConfigPath;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import me.minebuilders.clearlag.modules.ClearModule;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

@ConfigPath(path = "kill-mobs")
public class KillMobsClear extends ClearModule {

    @ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE)
    private EntityTable mobFilter;

    @ConfigValue
    private boolean removeNamed;

    @Override
    public boolean isRemovable(Entity e) {
        return e instanceof LivingEntity && !(e instanceof HumanEntity) && (removeNamed || e.getCustomName() == null) && !mobFilter.containsEntity(e);
    }

}