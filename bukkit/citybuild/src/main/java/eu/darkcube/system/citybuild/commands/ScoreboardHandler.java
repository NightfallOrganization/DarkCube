package eu.darkcube.system.citybuild.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

public class ScoreboardHandler {
	private static final String OBJECTIVE_NAME = "scoreboard";
	private final Map<UUID, Scoreboard> playerScoreboards = new HashMap<>();

	public void showPlayerLevelScoreboard(Player player) {
		Scoreboard scoreboard = playerScoreboards.get(player.getUniqueId());
		if (scoreboard == null) {
			scoreboard = player.getScoreboard();
			if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
				scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
				player.setScoreboard(scoreboard);
			}
			playerScoreboards.put(player.getUniqueId(), scoreboard);
		}

		Objective objective = scoreboard.getObjective(OBJECTIVE_NAME);
		if (objective == null) {
			objective = scoreboard.registerNewObjective(OBJECTIVE_NAME, "dummy",
					"" + "§7« §2Dark§aCube§7.§2eu §7»");
			objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		}

		// get the latest online time
		long roundedOnlineTime = Math.round(
				Citybuild.getInstance().getPlayerOnlineTimeTracker().getOnlineTime(player));

		// update the online time score
		for (String entry : scoreboard.getEntries()) {
			Score score = objective.getScore(entry);
			if (score.getScore() == 1) {
				scoreboard.resetScores(entry);
				objective.getScore("§7» " + roundedOnlineTime + " Minuten").setScore(1);
				break;
			}
		}

		// if scoreboard has no entries, initialize it
		if (scoreboard.getEntries().isEmpty()) {
			String[] lines = {ChatColor.RESET.toString(), "§7➥ §aRang§7:", "§7» Spieler",
					ChatColor.RESET.toString() + ChatColor.RESET.toString(), "§7➥ §aGilde§7:",
					"§7» [Gildenname]", ChatColor.RESET.toString() + ChatColor.RESET.toString()
					+ ChatColor.RESET.toString(), "§7➥ §aGold§7:", "§7» [Betrag]",
					ChatColor.RESET.toString() + ChatColor.RESET.toString()
							+ ChatColor.RESET.toString() + ChatColor.RESET.toString(),
					"§7➥ §aLevel§7:", "§7» " + player.getLevel(),
					ChatColor.RESET.toString() + ChatColor.RESET.toString()
							+ ChatColor.RESET.toString() + ChatColor.RESET.toString()
							+ ChatColor.RESET.toString() + ChatColor.RESET.toString(),
					"§7➥ §aSpielzeit§7:", "§7» " + roundedOnlineTime + " Minuten",};

			// Set scores for lines
			for (int i = 0; i < lines.length; i++) {
				objective.getScore(lines[i]).setScore(lines.length - i);
			}
		}
	}
}
