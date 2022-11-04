package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerPlayerDropItem extends Listener<PlayerDropItemEvent> {

	@Override
	@EventHandler
	public void handle(PlayerDropItemEvent e) {
		e.setCancelled(true);
	}
}