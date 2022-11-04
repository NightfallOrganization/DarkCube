package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ListenerInteract implements Listener {

	@EventHandler
	public void handle(InventoryClickEvent e) {
		e.setCancelled(true);
	}
}
