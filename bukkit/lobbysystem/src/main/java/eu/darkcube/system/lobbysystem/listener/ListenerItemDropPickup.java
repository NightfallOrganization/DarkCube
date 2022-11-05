package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerItemDropPickup extends BaseListener {

	@EventHandler
	public void handle(PlayerDropItemEvent e) {
		if (!UserWrapper.getUser(e.getPlayer().getUniqueId()).isBuildMode()) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void handle(PlayerPickupItemEvent e) {
		if (!UserWrapper.getUser(e.getPlayer().getUniqueId()).isBuildMode()) {
			e.setCancelled(true);
		}
	}
}
