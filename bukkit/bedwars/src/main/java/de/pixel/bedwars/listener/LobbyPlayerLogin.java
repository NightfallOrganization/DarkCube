package de.pixel.bedwars.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import de.pixel.bedwars.Main;

public class LobbyPlayerLogin implements Listener {

	@EventHandler
	public void handle(PlayerLoginEvent e) {
		if (Main.getInstance().getMaxPlayers() >= 2) {
			if (Main.getInstance().getMaxPlayers() <= Bukkit.getOnlinePlayers().size()) {
				e.disallow(Result.KICK_OTHER, "§cFull server");
			}
		}
	}
}
