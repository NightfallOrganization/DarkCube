package de.pixel.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.pixel.bedwars.Main;
import de.pixel.bedwars.team.Team;
import de.pixel.bedwars.util.Message;

public class LobbyPlayerJoin implements Listener {

	@EventHandler
	public void handle(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Player p = e.getPlayer();
		Main.getInstance().getLobby().setupPlayer(p);
		Main.sendMessage(Message.LOBBY_PLAYER_JOINED, t -> "§" + Team.getTeam(p).getNamecolor() + p.getName());
//		for (Player t : Bukkit.getOnlinePlayers()) {
//			t.sendMessage(
//					Message.LOBBY_PLAYER_JOINED.getMessage(t, "§" + Team.getTeam(p).getNamecolor() + p.getName()));
//		}
	}
}
