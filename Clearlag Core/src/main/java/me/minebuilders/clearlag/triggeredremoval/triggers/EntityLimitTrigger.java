package me.minebuilders.clearlag.triggeredremoval.triggers;

import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import me.minebuilders.clearlag.triggeredremoval.CleanerHandler;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Set;

/**
 * @author bob7l
 */
public class EntityLimitTrigger extends CleanerTrigger {

    @ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE)
    private EntityTable entityLimits;

    @ConfigValue
    private int limit;

    @ConfigValue(valueType = ConfigValueType.STRING_SET)
    private Set<String> worldFilter;

    public EntityLimitTrigger(CleanerHandler cleanerHandler) {
        super(cleanerHandler);
    }

    @Override
    public boolean shouldTrigger() {
        return countEntities() >= limit;
    }

    @Override
    public boolean isRecovered() {
        return super.isRecovered() && !shouldTrigger();
    }

    private int countEntities() {

        int entities = 0;

        for (World world : Bukkit.getWorlds()) {

            if (!worldFilter.contains(world.getName())) {

                for (Entity entity : world.getEntities()) {

                    if (entityLimits.containsEntity(entity))
                        ++entities;

                }
            }
        }

        return entities;
    }
}
