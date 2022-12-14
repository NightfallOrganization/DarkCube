package eu.darkcube.system.miners.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import eu.darkcube.system.miners.Miners;

public class ListenerPlayerLogin implements Listener {

	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent e) {
		switch (Miners.getGamephase()) {
		case 0:
			if (Bukkit.getOnlinePlayers().size() >= Miners.getMinersConfig().MAX_PLAYERS)
				e.disallow(Result.KICK_BANNED, "§cDieser Server ist voll!");
			break;
		default:
			break;
		}
	}

}
