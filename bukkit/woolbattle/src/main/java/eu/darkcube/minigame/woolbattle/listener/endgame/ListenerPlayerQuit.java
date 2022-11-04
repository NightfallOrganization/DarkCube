package eu.darkcube.minigame.woolbattle.listener.endgame;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.minigame.woolbattle.listener.Listener;

public class ListenerPlayerQuit extends Listener<PlayerQuitEvent> {

	@Override
	@EventHandler
	public void handle(PlayerQuitEvent e) {
		e.setQuitMessage(null);
	}
}