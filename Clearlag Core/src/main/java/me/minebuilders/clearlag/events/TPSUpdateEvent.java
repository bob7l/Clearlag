package me.minebuilders.clearlag.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class TPSUpdateEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	private final double tps;
	
	public TPSUpdateEvent(double tps) {
		this.tps = tps;
	}
	
	public double getTPS() {
		return tps;
	}
	
	@Override
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}
	
}
