package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.miners.player.Message;

public class ListenerPlayerQuit implements Listener {

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
		e.setQuitMessage(null);
		Miners.getPlayerManager().removePlayer(e.getPlayer());
		e.setQuitMessage(null);
		Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
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
		e.setLeaveMessage(null);
		if (Miners.getGamephase() != 3)
			Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
		Miners.getPlayerManager().removePlayer(e.getPlayer());
		e.setLeaveMessage(null);
		Miners.sendTranslatedMessageAll(Message.PLAYER_LEFT, e.getPlayer().getName());
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
