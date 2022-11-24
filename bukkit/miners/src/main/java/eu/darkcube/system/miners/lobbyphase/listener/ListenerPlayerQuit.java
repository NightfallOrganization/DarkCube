package eu.darkcube.system.miners.lobbyphase.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerQuit implements Listener {

	@EventHandler
	public void onPlayerJoin(PlayerQuitEvent e) {
		if (Miners.getGamephase() != 0)
			return;
		if (Miners.getLobbyPhase().getTimer().isRunning()) {
			if (Bukkit.getOnlinePlayers().size() == 2)
				Miners.getLobbyPhase().getTimer().cancel(false);
		}
	}

}
