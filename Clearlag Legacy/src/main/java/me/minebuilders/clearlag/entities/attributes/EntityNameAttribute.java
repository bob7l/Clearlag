package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * @author bob7l
 */
public class EntityNameAttribute extends EntityAttribute<Entity> {

    private final String name;

    public EntityNameAttribute(String name) {
        this.name = name;
    }

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity instanceof LivingEntity && name.equals(((LivingEntity) entity).getCustomName())));
    }

}
