package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;

/**
 * @author bob7l
 */
public class EntityMaterialAttribute extends EntityAttribute<Entity> {

    private final Material material;

    public EntityMaterialAttribute(Material material) {
        this.material = material;
    }

    @Override
    public boolean containsData(Entity entity) {
        return (!reversed == (material == ((Item)entity).getItemStack().getType()));
    }

}
