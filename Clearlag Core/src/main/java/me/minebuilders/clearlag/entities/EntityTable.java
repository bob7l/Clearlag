package me.minebuilders.clearlag.entities;

import me.minebuilders.clearlag.entities.attributes.EntityAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author bob7l
 */
public class EntityTable {

    private final List<EntityAttribute<Entity>>[] entityTable = new ArrayList[EntityType.values().length + 1];

    private static final ArrayList<EntityAttribute<Entity>> EMPTY_COLLECTION = new ArrayList<>(1);

    public boolean containsEntity(Entity entity) {

        List<EntityAttribute<Entity>> con = entityTable[entity.getType().ordinal()];

        if (con != null) {

            if (!con.isEmpty()) {

                for (EntityAttribute<Entity> e : con)
                    if (!e.containsData(entity))
                        return false;
            }

            return true;
        }

        return false;
    }


    public void setEntityAttributes(EntityType type, ArrayList<EntityAttribute<Entity>> entityAttributes) {
        entityTable[type.ordinal()] = entityAttributes;
    }

    public void setEntity(EntityType type) {
        entityTable[type.ordinal()] = EMPTY_COLLECTION;
    }
}
