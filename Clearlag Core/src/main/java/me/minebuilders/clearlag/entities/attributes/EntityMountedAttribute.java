package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public class EntityMountedAttribute extends EntityAttribute<Entity> {

    @Override
    public boolean containsData(Entity entity) {
        return (reversed == entity.isEmpty());
    }

}
