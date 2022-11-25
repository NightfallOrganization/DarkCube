package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Miners.getPlayerManager().removePlayer(e.getPlayer());
		switch (Miners.getGamephase()) {
		case 0:
			if (Miners.getLobbyPhase().getTimer().isRunning()) {
				if (Bukkit.getOnlinePlayers().size() == 2)
					Miners.getLobbyPhase().getTimer().cancel(false);
			}
			break;
		default:
			break;
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerKickEvent e) {
		Miners.getPlayerManager().removePlayer(e.getPlayer());
		switch (Miners.getGamephase()) {
		case 0:
			if (Miners.getLobbyPhase().getTimer().isRunning()) {
				if (Bukkit.getOnlinePlayers().size() == 2)
					Miners.getLobbyPhase().getTimer().cancel(false);
			}
			break;
		default:
			break;
		}
	}

}
