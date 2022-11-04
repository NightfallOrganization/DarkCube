package eu.darkcube.minigame.smash.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class LobbyBlock extends BaseListener {

	@EventHandler
	public void handle(BlockPlaceEvent e) {
		e.setCancelled(true);
	}

	@EventHandler
	public void handle(BlockBreakEvent e) {
		e.setCancelled(true);
	}
}
