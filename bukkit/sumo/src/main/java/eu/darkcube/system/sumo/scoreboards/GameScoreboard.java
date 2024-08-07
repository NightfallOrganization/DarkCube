/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.scoreboards;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.manager.LifeManager;
import eu.darkcube.system.sumo.manager.MapManager;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class GameScoreboard implements Listener {
    private MapManager mapManager;
    private PrefixManager prefixManager;
    private Sumo sumo;
    private static final String ENTRY_BLACK = "§0§f ";
    private static final String ENTRY_WHITE = "§f§f ";

    public GameScoreboard(Sumo sumo, MapManager mapManager, PrefixManager prefixManager) {
        this.sumo = sumo;
        this.mapManager = mapManager;
        this.prefixManager = prefixManager;
        // createGameScoreboard();
    }

    public void createGameScoreboard( Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());
        String scoreboardHardcore = Message.SCOREBOARD_HARDCORE_OFF.convertToString(user);
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager == null) {
            throw new IllegalStateException("ScoreboardManager nicht verfügbar");
        }

        var scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);




        var objective = scoreboard.registerNewObjective("game", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.WHITE + "« " + ChatColor.DARK_GRAY + "Dark" + ChatColor.GRAY + "Cube" + ChatColor.WHITE + "." + ChatColor.DARK_GRAY + "eu" + ChatColor.WHITE + " »");

        Score space = objective.getScore(" ");
        space.setScore(3);

        var blackTeam = scoreboard.registerNewTeam("black_team");
        blackTeam.setPrefix("§7×");
        blackTeam.addEntry(ENTRY_BLACK);
        objective.getScore(ENTRY_BLACK).setScore(2);

        var whiteTeam = scoreboard.registerNewTeam("white_team");
        whiteTeam.setPrefix("§7×");
        whiteTeam.addEntry(ENTRY_WHITE);
        objective.getScore(ENTRY_WHITE).setScore(1);

        updateBlackLives(player);
        updateWhiteLives(player);

        prefixManager.setupPlayer(player);
    }

    public void updateBlackLives() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateBlackLives(onlinePlayer);
        }
    }

    public void updateWhiteLives() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateWhiteLives(onlinePlayer);
        }
    }

    public void updateBlackLives(Player player) {
        var team = player.getScoreboard().getTeam("black_team");
        var lives = sumo.getLifeManager().getTeamLives(TeamManager.TEAM_BLACK);
        team.setSuffix(Message.LIVES_SUFFIX_BLACK.convertToString(player, lives));
    }

    public void updateWhiteLives(Player player) {
        var team = player.getScoreboard().getTeam("white_team");
        var lives = sumo.getLifeManager().getTeamLives(TeamManager.TEAM_WHITE);
        team.setSuffix(Message.LIVES_SUFFIX_WHITE.convertToString(player, lives));
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World newWorld = player.getWorld();

        if (newWorld.equals(mapManager.getActiveWorld())) {
            createGameScoreboard(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        World playerWorld = player.getWorld();

        if (playerWorld.equals(mapManager.getActiveWorld())) {
            createGameScoreboard(player);
        }
    }
}
