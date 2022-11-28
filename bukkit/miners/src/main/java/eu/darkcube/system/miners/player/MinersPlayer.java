package eu.darkcube.system.miners.player;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import eu.darkcube.system.language.core.Language;
import eu.darkcube.system.miners.Miners;

public class MinersPlayer {

	private Player player;

	private Team scoreboardTeam;

	private int kills = 0;

	public MinersPlayer(Player player) {
		this.player = player;
		scoreboardTeam = Bukkit.getScoreboardManager().getMainScoreboard().registerNewTeam(player.getName());
		scoreboardTeam.addEntry(player.getName());
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	public int getKills() {
		return kills;
	}

	public Player getPlayer() {
		return player;
	}

	public void updatePlayerName() {
		String prefix = Miners.getTeamManager().getPlayerTeam(player) == 0 ? ""
				: ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + Miners.getTeamManager().getPlayerTeam(player)
						+ ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

		scoreboardTeam.setPrefix(prefix);
		player.setPlayerListName(prefix + player.getName());
		player.setCustomName(prefix + player.getName());
	}

	public Team getScoreboardTeam() {
		return scoreboardTeam;
	}

	public Language getLanguage() {
		return Language.ENGLISH;
	}

}
