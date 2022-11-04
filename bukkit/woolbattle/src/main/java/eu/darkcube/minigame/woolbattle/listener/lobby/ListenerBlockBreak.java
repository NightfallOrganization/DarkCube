package eu.darkcube.minigame.woolbattle.listener.lobby;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerBlockBreak extends Listener<BlockBreakEvent> {

	@Override
	@EventHandler
	public void handle(BlockBreakEvent e) {
		e.setCancelled(true);
	}
}