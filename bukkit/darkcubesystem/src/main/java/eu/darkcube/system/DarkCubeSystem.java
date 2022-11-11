package eu.darkcube.system;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

public final class DarkCubeSystem extends DarkCubePlugin implements Listener {

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void handle(PlayerKickEvent event) {
		if (event.getReason() == "disconnect.spam") {
			event.setCancelled(true);
		}
	}

}
