package eu.darkcube.system.miners.player;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import eu.darkcube.system.miners.Miners;

public class PlayerManager {

	private Map<Player, MinersPlayer> players;

	public PlayerManager() {
		players = new HashMap<>();
	}

	public MinersPlayer getMinersPlayer(Player p) {
		return players.get(p);
	}

	public Map<Player, MinersPlayer> getPlayers() {
		return players;
	}

	public void addPlayer(Player p) {
		if (!players.containsKey(p))
			players.put(p, new MinersPlayer(p));
	}

	public void removePlayer(Player p) {
		Miners.getTeamManager().removePlayerFromTeam(p);
		players.get(p).getScoreboardTeam().removeEntry(p.getName());
		players.get(p).getScoreboardTeam().unregister();
		players.remove(p);
	}

}