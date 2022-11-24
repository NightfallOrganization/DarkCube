package eu.darkcube.system.miners.lobbyphase.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (Miners.getGamephase() != 0)
			return;
	}

}
