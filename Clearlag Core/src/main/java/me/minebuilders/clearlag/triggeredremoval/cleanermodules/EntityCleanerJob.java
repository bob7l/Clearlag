package me.minebuilders.clearlag.triggeredremoval.cleanermodules;

import me.minebuilders.clearlag.annotations.AutoWire;
import me.minebuilders.clearlag.annotations.ConfigValue;
import me.minebuilders.clearlag.config.ConfigValueType;
import me.minebuilders.clearlag.entities.EntityTable;
import me.minebuilders.clearlag.managers.EntityManager;
import me.minebuilders.clearlag.modules.ClearModule;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.Set;

/**
 * @author bob7l
 */
public class EntityCleanerJob extends ClearModule {

    @ConfigValue(valueType = ConfigValueType.ENTITY_TYPE_TABLE)
    private EntityTable removeEntities;

    @ConfigValue(valueType = ConfigValueType.STRING_SET)
    private Set<String> worldFilter;

    @AutoWire
    private EntityManager entityManager;

    @Override
    public void setEnabled() {
        super.setEnabled();

        entityManager.removeEntities(this);

        setDisabled();
    }

    @Override
    public boolean isRemovable(Entity e) {
        return removeEntities.containsEntity(e);
    }

    @Override
    public boolean isWorldEnabled(World w) {
        return !worldFilter.contains(w.getName());
    }
}
