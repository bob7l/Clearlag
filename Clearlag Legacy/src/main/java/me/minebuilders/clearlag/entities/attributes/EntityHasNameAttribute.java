package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

/**
 * @author bob7l
 */
public class EntityHasNameAttribute extends EntityAttribute<Entity> {

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity instanceof LivingEntity && ((LivingEntity) entity).getCustomName() != null));
    }

}
