package me.minebuilders.clearlag.tasks;

import me.minebuilders.clearlag.Clearlag;
import me.minebuilders.clearlag.modules.EventModule;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

public class WarnTask extends EventModule {

	private final boolean resetConfig;

	public WarnTask(boolean resetConfig) {
		this.resetConfig = resetConfig;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();

		if (p.hasPermission("lagg.clear")) {
			p.sendMessage("§8§l]=============(§7§lClearlag Updated§8§l)=============[");
			p.sendMessage("§6§lNew Version: §7§l" + Clearlag.getInstance().getDescription().getVersion());
			p.sendMessage("§6Please check §7§nhttp://dev.bukkit.org/bukkit-plugins/clearlagg/");

			if (resetConfig) {
				p.sendMessage("§cWARNING: §7Clearlag was forced to reset your config due to an update!");
				p.sendMessage("§cWARNING: §7Please re-modify your clearlag config!");
				p.sendMessage("§8This message will go away once you either reboot, or reload clearlag!");
			}
		}
	}
}