package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public class EntityHasMetaAttribute extends EntityAttribute<Entity> {

    private final String meta;

    public EntityHasMetaAttribute(String meta) {
        this.meta = meta;
    }

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (entity.hasMetadata(meta)));
    }

}

