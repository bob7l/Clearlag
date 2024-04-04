package me.minebuilders.clearlag.events;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class EntityRemoveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private final World world;
	private final List<Entity> entities;
	
	public EntityRemoveEvent(List<Entity> entities, World w) {
		this.entities = entities;
		this.world = w;
	}
	
	public void addEntity(Entity e) {
		entities.add(e);
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
	}
	
	public List<Entity> getEntityList() {
		return entities;
	}
	
	public World getWorld() {
		return world;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
