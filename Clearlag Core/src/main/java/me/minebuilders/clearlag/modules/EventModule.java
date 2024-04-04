package me.minebuilders.clearlag.modules;

import me.minebuilders.clearlag.Clearlag;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class EventModule extends ClearlagModule implements Listener {

	@Override
	public void setEnabled() {
		super.setEnabled();

		Bukkit.getPluginManager().registerEvents(this, Clearlag.getInstance());
	}

	@Override
	public void setDisabled() {
		super.setDisabled();

		HandlerList.unregisterAll(this);
	}

}
