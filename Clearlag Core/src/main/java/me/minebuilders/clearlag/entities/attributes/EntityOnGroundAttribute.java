package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public class EntityOnGroundAttribute extends EntityAttribute<Entity> {

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity.isOnGround()));
    }

}
