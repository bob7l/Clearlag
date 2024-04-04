package me.minebuilders.clearlag.entities;

import me.minebuilders.clearlag.entities.attributes.EntityAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author bob7l
 */
public class EntityMap<T> {

    private final Node<T>[] entityTable = new Node[EntityType.values().length + 1];

    public boolean containsEntity(Entity entity) {

        Node<T> node = entityTable[entity.getType().ordinal()];

        if (node != null) {

            for (EntityAttribute<Entity> e : node.attributes)
                if (!e.containsData(entity))
                    return false;

            return true;
        }

        return false;
    }

    public T getValue(Entity entity) {

        Node<T> node = entityTable[entity.getType().ordinal()];

        if (node != null) {

            for (EntityAttribute<Entity> e : node.attributes)
                if (!e.containsData(entity))
                    return null;

            return node.value;
        }

        return null;
    }

    public void set(EntityType type, ArrayList<EntityAttribute<Entity>> entityAttributes, T value) {
        entityTable[type.ordinal()] = new Node<>(entityAttributes, value);
    }

    public void set(EntityType type, T value) {
        entityTable[type.ordinal()] = new Node<>(Collections.emptyList(), value);
    }

    private static class Node<T> {

        private final List<EntityAttribute<Entity>> attributes;

        private final T value;

        private Node(List<EntityAttribute<Entity>> attributes, T value) {
            this.attributes = attributes;
            this.value = value;
        }
    }
}
