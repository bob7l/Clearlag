package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public class EntityHasNameAttribute extends EntityAttribute<Entity> {

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity.getCustomName() != null));
    }

}
