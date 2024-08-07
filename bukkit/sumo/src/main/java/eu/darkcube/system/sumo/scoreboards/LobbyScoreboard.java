/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.scoreboards;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.other.GameStates;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard implements Listener {

    private static final String ENTRY_TIME = "§1§f";
    private static final String ENTRY_MAP = "§2§f";
    private static final String ENTRY_ONLINE = "§3§f";
    private String activeMap = "???";
    private Sumo sumo;
    private PrefixManager prefixManager;

    public LobbyScoreboard(Sumo sumo, PrefixManager prefixManager) {
        this.sumo = sumo;
        this.prefixManager = prefixManager;
        // createLobbyScoreboard();
    }

    public void createLobbyScoreboardForAll() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            createLobbyScoreboard(onlinePlayer);
        }
    }

    public void createLobbyScoreboard(Player player) {
        User user = UserAPI.instance().user(player.getUniqueId());
        String scoreboardHardcore = Message.SCOREBOARD_HARDCORE_OFF.convertToString(user);
        String scoreboardTime = Message.SCOREBOARD_TIME.convertToString(user);
        String scoreboardNeeded = Message.SCOREBOARD_PLAYER_NEEDED.convertToString(user);
        ScoreboardManager manager = Bukkit.getScoreboardManager();

        if (manager == null) {
            throw new IllegalStateException("ScoreboardManager nicht verfügbar");
        }
        var scoreboard = player.getScoreboard();
        scoreboard = manager.getNewScoreboard();
        player.setScoreboard(scoreboard);

        var objective = scoreboard.registerNewObjective("timer", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.WHITE + "« " + ChatColor.DARK_GRAY + "Dark" + ChatColor.GRAY + "Cube" + ChatColor.WHITE + "." + ChatColor.DARK_GRAY + "eu" + ChatColor.WHITE + " »");

        Score space = objective.getScore(" ");
        space.setScore(6);

        Score epGlitch = objective.getScore(scoreboardHardcore);
        epGlitch.setScore(5);

        var time = scoreboard.registerNewTeam("lobby_time");
        time.setPrefix(scoreboardTime);
        time.addEntry(ENTRY_TIME);
        objective.getScore(ENTRY_TIME).setScore(4);

        var map = scoreboard.registerNewTeam("lobby_map");
        map.setPrefix("§7Map: ");
        map.addEntry(ENTRY_MAP);
        objective.getScore(ENTRY_MAP).setScore(3);

        Score benoetigt = objective.getScore(scoreboardNeeded);
        benoetigt.setScore(2);

        var online = scoreboard.registerNewTeam("lobby_online");
        online.setPrefix("§7Online: ");
        online.addEntry(ENTRY_ONLINE);
        objective.getScore(ENTRY_ONLINE).setScore(1);

        updateTime(player, 15);
        updateMap(player);
        updateOnlinePlayers(player);

        // setup prefixes
        prefixManager.setupPlayer(player);
        prefixManager.setupOtherPlayers(player);
    }

    public void updateTime(int time) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateTime(onlinePlayer, time);
        }
    }

    public void updateTime(Player player, int time) {
        var team = player.getScoreboard().getTeam("lobby_time");
        team.setSuffix(Integer.toString(time));
    }

    public void updateMap(String map) {
        this.activeMap = map;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateMap(onlinePlayer);
        }
    }

    public void updateMap(Player player) {
        var team = player.getScoreboard().getTeam("lobby_map");
        team.setSuffix(activeMap);
    }

    public void updateOnlinePlayers() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            updateOnlinePlayers(onlinePlayer);
        }
    }

    public void updateOnlinePlayers(Player player) {
        var team = player.getScoreboard().getTeam("lobby_online");
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        team.setSuffix(Integer.toString(onlinePlayers));
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (event.getPlayer().getWorld().getName().equals("world") && GameStates.isState(GameStates.STARTING)) {
            createLobbyScoreboardForAll();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            createLobbyScoreboard(player);
            updateOnlinePlayers(player);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            updateTime(15);
            Bukkit.getScheduler().runTaskLater(sumo, this::updateOnlinePlayers, 0L);
        }
    }

}
