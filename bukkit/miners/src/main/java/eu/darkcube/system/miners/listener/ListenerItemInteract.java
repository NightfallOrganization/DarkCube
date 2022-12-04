package eu.darkcube.system.miners.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ListenerItemInteract implements Listener {

	@EventHandler
	public void onItemInteract(PlayerInteractEvent e) {
		if (e.getMaterial().equals(Material.WORKBENCH)) {
			e.setCancelled(true);
			e.getPlayer().openWorkbench(null, true);
		}
	}

}
