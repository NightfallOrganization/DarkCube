/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.player;

import eu.darkcube.system.miners.Miners;
import eu.darkcube.system.util.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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

	public void updatePlayer() {
		String prefix = Miners.getTeamManager().getPlayerTeam(player) == 0 ? ChatColor.GRAY.toString()
				: ChatColor.DARK_GRAY + "[" + ChatColor.AQUA + Miners.getTeamManager().getPlayerTeam(player)
						+ ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

		scoreboardTeam.setPrefix(prefix);
		player.setPlayerListName(prefix + player.getName());
		player.setCustomName(prefix + player.getName());

		if (Miners.getTeamManager().getPlayerTeam(player) == 0) { // hide spectators from non-specs
			if (Miners.getGamephase() != 0 && Miners.getGamephase() != 3) {
				player.setAllowFlight(true);
				Bukkit.getOnlinePlayers().forEach(p -> {
					if (Miners.getTeamManager().getPlayerTeam(p) != 0)
						p.hidePlayer(player);
				});
			}
		} else { // show non-spectators to everyone
			Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
			player.setAllowFlight(false);
		}
	}

	public Team getScoreboardTeam() {
		return scoreboardTeam;
	}

	public Language getLanguage() {
		return Language.ENGLISH;
	}

}
