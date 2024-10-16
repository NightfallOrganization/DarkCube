/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.prefix;

import eu.darkcube.system.sumo.Sumo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class PrefixManager {
    private Sumo sumo;

    public PrefixManager(Sumo sumo) {
        this.sumo = sumo;
    }

    public void removePlayerPrefix(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            String teamName = player.getName();

            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                team.unregister();
            }
        }
    }

    public void setupOtherPlayers(Player player) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard = onlinePlayer.getScoreboard();
            setup(scoreboard, player);
        }
    }

    public void setupPlayer(Player player) {
        var scoreboard = player.getScoreboard();
        var doneSelf = false;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer == player) doneSelf = true;
            setup(scoreboard, onlinePlayer);
        }
        if (!doneSelf) {
            setup(scoreboard, player);
        }
    }

    public String calculatePrefix(Player player) {
        ChatColor teamColor = sumo.getTeamManager().getPlayerTeam(player.getUniqueId());
        return teamColor.toString();
    }

    private void setup(Scoreboard scoreboard, Player player) {
        String teamName = player.getName();

        Team team = scoreboard.getTeam(teamName);
        if (team == null) {
            team = scoreboard.registerNewTeam(teamName);
        }

        team.setPrefix(calculatePrefix(player));
        if (!team.hasEntry(player.getName())) {
            team.addEntry(player.getName());
        }
    }
}
