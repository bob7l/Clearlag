package me.minebuilders.clearlag.entities.attributes;

import org.bukkit.entity.Entity;

/**
 * @author bob7l
 */
public abstract class EntityAttribute<T extends Entity> {

    protected boolean reversed = false;

    public boolean isReversed() {
        return reversed;
    }

    public void setReversed(boolean reversed) {
        this.reversed = reversed;
    }

    public abstract boolean containsData(T entity);

}
