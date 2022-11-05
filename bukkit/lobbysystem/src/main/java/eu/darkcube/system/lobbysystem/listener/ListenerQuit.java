package eu.darkcube.system.lobbysystem.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerQuit extends BaseListener {

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		Lobby.getInstance().getDataManager().setUserPos(e.getPlayer().getUniqueId(), e.getPlayer().getLocation());
		UserWrapper.unloadUser(UserWrapper.getUser(e.getPlayer().getUniqueId()));
	}
}
