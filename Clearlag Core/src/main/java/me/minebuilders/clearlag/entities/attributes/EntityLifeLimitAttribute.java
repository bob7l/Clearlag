package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public class EntityLifeLimitAttribute extends EntityAttribute<Entity> {

    private final int lifeLimit;

    public EntityLifeLimitAttribute(int lifeLimit) {
        this.lifeLimit = lifeLimit;
    }

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity.getTicksLived() >= lifeLimit));
    }


}
