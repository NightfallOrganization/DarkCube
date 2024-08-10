/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util;

import eu.darkcube.system.woolmania.WoolMania;
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
    private static final String ENTRY_LEVEL = "§1";
    private static final String ENTRY_MONEY = "§2";
    private static final String ENTRY_WORLD = "§3";
    private static final String ENTRY_FARMED = "§4";
    private static final String ENTRY_BOOSTER = "§5";

    public void createGameScoreboard( Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);

        var objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.GRAY + "« " + ChatColor.DARK_AQUA + "Dark" + ChatColor.AQUA + "Cube" + ChatColor.GRAY + "." + ChatColor.DARK_AQUA + "eu" + ChatColor.GRAY + " »");

        Score space1 = objective.getScore(" ");
        space1.setScore(15);

        Score levelnameTeam = objective.getScore("§7Dein Level:");
        levelnameTeam.setScore(14);

        Team levelTeam = scoreboard.registerNewTeam("level_team");
        levelTeam.addEntry(ENTRY_LEVEL);
        objective.getScore(ENTRY_LEVEL).setScore(13);

        Score space2 = objective.getScore("  ");
        space2.setScore(12);

        Score moneynameTeam = objective.getScore("§7Deine Zenum:");
        moneynameTeam.setScore(11);

        Team moneyTeam = scoreboard.registerNewTeam("money_team");
        moneyTeam.addEntry(ENTRY_MONEY);
        objective.getScore(ENTRY_MONEY).setScore(10);

        Score space3 = objective.getScore("   ");
        space3.setScore(9);

        Score worldnameTeam = objective.getScore("§7Aktuelle Welt:");
        worldnameTeam.setScore(8);

        Team worldTeam = scoreboard.registerNewTeam("world_team");
        worldTeam.addEntry(ENTRY_WORLD);
        objective.getScore(ENTRY_WORLD).setScore(7);

        Score space4 = objective.getScore("    ");
        space4.setScore(6);

        Score farmednameTeam = objective.getScore("§7Abgebaute Blöcke:");
        farmednameTeam.setScore(5);

        Team farmedTeam = scoreboard.registerNewTeam("farmed_team");
        farmedTeam.addEntry(ENTRY_FARMED);
        objective.getScore(ENTRY_FARMED).setScore(4);

        Score space5 = objective.getScore("     ");
        space5.setScore(3);

        Score boosternameTeam = objective.getScore("§7Privater Booster:");
        boosternameTeam.setScore(2);

        Team boosterTeam = scoreboard.registerNewTeam("booster_team");
        boosterTeam.addEntry(ENTRY_BOOSTER);
        objective.getScore(ENTRY_BOOSTER).setScore(1);

        updateLevel(player);
        updateMoney(player);
        updateWorld(player);
        updateFarmed(player);
        updateBooster(player);
    }

    public void updateLevel(Player player) {
        var team = player.getScoreboard().getTeam("level_team");
        int level = WoolMania.getStaticPlayer(player).getLevel();
        team.setSuffix("§7» §b" + level);
    }

    public void updateMoney(Player player) {
        var team = player.getScoreboard().getTeam("money_team");
        int money = WoolMania.getStaticPlayer(player).getMoney();
        team.setSuffix("§7» §b" + money);
    }

    public void updateWorld(Player player) {
        var team = player.getScoreboard().getTeam("world_team");
        team.setSuffix("§7» §bNONE");
    }

    public void updateFarmed(Player player) {
        var team = player.getScoreboard().getTeam("farmed_team");
        int farmedBlocks = WoolMania.getStaticPlayer(player).getFarmedBlocks();
        team.setSuffix("§7» §b" + farmedBlocks);
    }

    public void updateBooster(Player player) {
        var team = player.getScoreboard().getTeam("booster_team");
        int privateBooster = WoolMania.getStaticPlayer(player).getPrivateBooster();
        if (privateBooster <= 1) {
            team.setSuffix("§7» §bNone");
        } else {
            team.setSuffix("§7» §b" + privateBooster +".0x");
        }
    }

    // public void updateWhiteLives(Player player) {
    //     var team = player.getScoreboard().getTeam("white_team");
    //     var lives = sumo.getLifeManager().getTeamLives(TeamManager.TEAM_WHITE);
    //     team.setSuffix(Message.LIVES_SUFFIX_WHITE.convertToString(player, lives));
    // }

    // public void deleteGameScoreboard() {
    //     ScoreboardManager manager = Bukkit.getScoreboardManager();
    //     Scoreboard scoreboard = manager.getMainScoreboard();
    //     Objective objective = scoreboard.getObjective("game");
    //     if (objective != null) {
    //         objective.unregister();
    //     }
    // }

}
