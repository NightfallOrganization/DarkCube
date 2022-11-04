package eu.darkcube.minigame.woolbattle.listener.ingame;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerItemDrop extends Listener<PlayerDropItemEvent> {
	@Override
	@EventHandler
	public void handle(PlayerDropItemEvent e) {
		if (e.getItemDrop().getItemStack().getType() != Material.WOOL) {
			e.setCancelled(true);
		}
	}
}