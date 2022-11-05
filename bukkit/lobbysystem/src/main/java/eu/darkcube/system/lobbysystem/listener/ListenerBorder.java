package eu.darkcube.system.lobbysystem.listener;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.user.UserWrapper;

public class ListenerBorder extends BaseListener {

	private Map<Player, Integer> failes = new HashMap<>();

	@EventHandler
	public void handle(PlayerMoveEvent e) {
		if (!UserWrapper.getUser(e.getPlayer().getUniqueId()).isBuildMode()
				&& Lobby.getInstance().getDataManager().getBorder().isOutside(e.getPlayer())) {
			failes.put(e.getPlayer(), failes.getOrDefault(e.getPlayer(), 0) + 1);
			if (failes.get(e.getPlayer()) > 10) {
				e.getPlayer().teleport(Lobby.getInstance().getDataManager().getSpawn());
			} else {
				Lobby.getInstance().getDataManager().getBorder().boost(e.getPlayer());
			}
		} else {
			failes.remove(e.getPlayer());
		}
	}

	@EventHandler
	public void handle(PlayerQuitEvent e) {
		failes.remove(e.getPlayer());
	}
}
