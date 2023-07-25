package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardHandler {

	public void showPlayerLevelScoreboard(Player player) {
		Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective objective = scoreboard.registerNewObjective("test", "dummy", ChatColor.GREEN + "Spieler Level");

		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		Score score = objective.getScore(ChatColor.GOLD + "Level:");
		score.setScore(player.getLevel());

		player.setScoreboard(scoreboard);
	}
}
