package eu.darkcube.system.miners.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerJoin implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Miners.getPlayerManager().addPlayer(e.getPlayer());
	}

}
