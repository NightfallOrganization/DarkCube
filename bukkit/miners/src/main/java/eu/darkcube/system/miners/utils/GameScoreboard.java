/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.utils;

import eu.darkcube.system.miners.utils.message.Message;
import io.papermc.paper.scoreboard.numbers.NumberFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameScoreboard implements Listener {
    private static final String ENTRY_HARDCORE = "§1";
    private static final String ENTRY_TIME = "§2";
    private static final String ENTRY_ABILITY = "§3";
    private static final String ENTRY_NEEDED = "§4";
    private static final String ENTRY_ONLINE = "§5";

    public void createGameScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = player.getScoreboard();
        if (scoreboard == manager.getMainScoreboard()) {
            scoreboard = manager.getNewScoreboard();
            player.setScoreboard(scoreboard);
        }

        var objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.numberFormat(NumberFormat.blank());
        objective.setDisplayName(ChatColor.GRAY + "« " + ChatColor.GOLD + "Dark" + ChatColor.YELLOW + "Cube" + ChatColor.GRAY + "." + ChatColor.GOLD + "eu" + ChatColor.GRAY + " »");

        Score space1 = objective.getScore(" ");
        space1.setScore(15);

        Score hardcoreNameTeam = objective.getScore("§7Error 14:");
        hardcoreNameTeam.customName(Message.SCOREBOARD_HARDCORE.getBukkit(player));
        hardcoreNameTeam.setScore(14);

        Team hardcoreTeam = scoreboard.registerNewTeam("hardcore_team");
        hardcoreTeam.addEntry(ENTRY_HARDCORE);
        objective.getScore(ENTRY_HARDCORE).setScore(13);

        Score space2 = objective.getScore("  ");
        space2.setScore(12);

        Score timeNameTeam = objective.getScore("§7Error 11:");
        timeNameTeam.customName(Message.SCOREBOARD_TIME.getBukkit(player));
        timeNameTeam.setScore(11);

        Team timeTeam = scoreboard.registerNewTeam("time_team");
        timeTeam.addEntry(ENTRY_TIME);
        objective.getScore(ENTRY_TIME).setScore(10);

        Score space3 = objective.getScore("   ");
        space3.setScore(9);

        Score abilityNameTeam = objective.getScore("§7Error 8:");
        abilityNameTeam.customName(Message.SCOREBOARD_ABILITY.getBukkit(player));
        abilityNameTeam.setScore(8);

        Team abilityTeam = scoreboard.registerNewTeam("ability_team");
        abilityTeam.addEntry(ENTRY_ABILITY);
        objective.getScore(ENTRY_ABILITY).setScore(7);

        Score space4 = objective.getScore("    ");
        space4.setScore(6);

        Score neededNameTeam = objective.getScore("§7Error 5:");
        neededNameTeam.customName(Message.SCOREBOARD_NEEDED.getBukkit(player));
        neededNameTeam.setScore(5);

        Team neededTeam = scoreboard.registerNewTeam("needed_team");
        neededTeam.addEntry(ENTRY_NEEDED);
        objective.getScore(ENTRY_NEEDED).setScore(4);

        Score space5 = objective.getScore("     ");
        space5.setScore(3);

        Score onlineNameTeam = objective.getScore("§7Error 2:");
        onlineNameTeam.customName(Message.SCOREBOARD_ONLINE.getBukkit(player));
        onlineNameTeam.setScore(2);

        Team onlineTeam = scoreboard.registerNewTeam("online_team");
        onlineTeam.addEntry(ENTRY_ONLINE);
        objective.getScore(ENTRY_ONLINE).setScore(1);

        updateHardcore(player);
        updateTime(player);
        updateAbility(player);
        updateNeeded(player);
        updateOnline(player);
    }

    public void updateHardcore(Player player) {
        var team = player.getScoreboard().getTeam("hardcore_team");
        team.setSuffix("§7» §eOFF");
    }

    public void updateTime(Player player) {
        var team = player.getScoreboard().getTeam("time_team");
        team.setSuffix("§7» §e0");
    }

    public void updateAbility(Player player) {
        var team = player.getScoreboard().getTeam("ability_team");
        MinersPlayer minersPlayer = new MinersPlayer(player);
        team.setSuffix("§7» §e" + minersPlayer.getActiveAbility().getName());
    }

    public void updateNeeded(Player player) {
        var team = player.getScoreboard().getTeam("needed_team");
        team.setSuffix("§7» §e2");
    }

    public void updateOnline(Player player) {
        var team = player.getScoreboard().getTeam("online_team");
        team.setSuffix("§7» §e" + Bukkit.getOnlinePlayers().size());
    }

    public void deleteGameScoreboard(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective objective = scoreboard.getObjective("game");
        if (objective != null) {
            objective.unregister();
        }
        deleteTeam(scoreboard, "hardcore_team");
        deleteTeam(scoreboard, "time_team");
        deleteTeam(scoreboard, "ability_team");
        deleteTeam(scoreboard, "needed_team");
        deleteTeam(scoreboard, "online_team");
    }

    private void deleteTeam(Scoreboard scoreboard, String teamName) {
        Team team = scoreboard.getTeam(teamName);
        if (team == null) return;
        team.unregister();
    }
}
