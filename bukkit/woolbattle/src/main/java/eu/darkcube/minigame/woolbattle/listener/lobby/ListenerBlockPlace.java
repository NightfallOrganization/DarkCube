package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerBlockPlace extends Listener<BlockPlaceEvent> {

	@Override
	@EventHandler
	public void handle(BlockPlaceEvent e) {
		e.setCancelled(true);
	}
}