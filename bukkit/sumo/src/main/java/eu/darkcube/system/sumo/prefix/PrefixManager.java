package eu.darkcube.system.sumo.prefix;

import eu.darkcube.system.sumo.manager.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PrefixManager {
    private TeamManager teamManager;

    public PrefixManager(TeamManager teamManager) {
        this.teamManager = teamManager;
    }

    public void setPlayerPrefix(Player player) {
        ChatColor teamColor = teamManager.getPlayerTeam(player.getUniqueId());
        String coloredPrefix = teamColor + "";

        updatePlayerNametag(player, coloredPrefix);
    }

    public void removePlayerPrefix(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        String teamName = player.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team != null) {
            team.removeEntry(player.getName());
            if (team.getSize() == 0) {
                team.unregister();
            }
        }
    }

    private void updatePlayerNametag(Player player, String prefix) {
        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        Scoreboard scoreboard = player.getScoreboard();
        String teamName = player.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(prefix);
        team.addEntry(player.getName());

    }
}
