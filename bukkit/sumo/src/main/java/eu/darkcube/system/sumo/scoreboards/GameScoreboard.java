/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.scoreboards;

import eu.darkcube.system.sumo.manager.LifeManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.ruler.MainRuler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class GameScoreboard implements Listener {
    private MainRuler mainRuler;
    private Scoreboard scoreboard;
    private Objective objective;
    private Team blackTeam;
    private Team whiteTeam;
    private static final String ENTRY_BLACK = "§0§f";
    private static final String ENTRY_WHITE = "§f§f";

    public GameScoreboard(MainRuler mainRuler) {
        this.mainRuler = mainRuler;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        blackTeam = scoreboard.registerNewTeam("black_team");
        blackTeam.setPrefix("§0Schwarz: ");
        blackTeam.addEntry(ENTRY_BLACK);
        objective.getScore(ENTRY_BLACK).setScore(2);

        whiteTeam = scoreboard.registerNewTeam("white_team");
        whiteTeam.setPrefix("§fWeiß: ");
        whiteTeam.addEntry(ENTRY_WHITE);
        objective.getScore(ENTRY_WHITE).setScore(1);
    }

    public void updateBlackLives(int lives) {
        blackTeam.setSuffix(" " + lives);
    }

    public void updateWhiteLives(int lives) {
        whiteTeam.setSuffix(" " + lives);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World newWorld = player.getWorld();

        if (newWorld.equals(mainRuler.getActiveWorld())) {
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World playerWorld = player.getWorld();

        if (playerWorld.equals(mainRuler.getActiveWorld())) {
            player.setScoreboard(scoreboard);
        }
    }

}
