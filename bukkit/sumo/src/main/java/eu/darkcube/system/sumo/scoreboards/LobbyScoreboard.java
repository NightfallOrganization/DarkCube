/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.scoreboards;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard implements Listener {

    private static final String ENTRY_TIME = "§1§f";
    private static final String ENTRY_MAP = "§2§f";
    private static final String ENTRY_ONLINE = "§3§f";

    private Sumo sumo;
    private Scoreboard scoreboard;
    private Objective objective;
    private Team time;
    private Team map;
    private Team online;

    public LobbyScoreboard(Sumo sumo) {
        this.sumo = sumo;
        createScoreboard();
        Bukkit.getPluginManager().registerEvents(this, sumo);
    }

    private void createScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if (manager == null) {
            throw new IllegalStateException("ScoreboardManager nicht verfügbar");
        }

        scoreboard = manager.getNewScoreboard();
        objective = scoreboard.registerNewObjective("timer", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.WHITE + "« " + ChatColor.DARK_GRAY + "Dark" + ChatColor.GRAY + "Cube" + ChatColor.WHITE + "." + ChatColor.DARK_GRAY + "eu" + ChatColor.WHITE + " »");

        Score space = objective.getScore(" ");
        space.setScore(6);

        Score epGlitch = objective.getScore(ChatColor.GRAY + "Hardcore: " + ChatColor.DARK_GRAY + "Aus");
        epGlitch.setScore(5);

        time = scoreboard.registerNewTeam("lobby_time");
        time.setPrefix("§7Zeit: ");
        time.addEntry(ENTRY_TIME);
        objective.getScore(ENTRY_TIME).setScore(4);

        map = scoreboard.registerNewTeam("lobby_map");
        map.setPrefix("§7Map: ");
        map.addEntry(ENTRY_MAP);
        objective.getScore(ENTRY_MAP).setScore(3);

        Score benoetigt = objective.getScore(ChatColor.GRAY + "Benötigt: " + ChatColor.WHITE + "2");
        benoetigt.setScore(2);

        online = scoreboard.registerNewTeam("lobby_online");
        online.setPrefix("§7Online: ");
        online.addEntry(ENTRY_ONLINE);
        objective.getScore(ENTRY_ONLINE).setScore(1);

        updateTime(0);
        updateOnlinePlayers();
    }

    public void updateTime(int time) {
        if (objective != null) {
            this.time.setSuffix(Integer.toString(time));
        }
    }

    public void updateMap(String map) {
        if (objective != null) {
            this.map.setSuffix(map);
        }
    }

    public void updateOnlinePlayers() {
        if (objective != null) {
            int onlinePlayers = Bukkit.getOnlinePlayers().size();
            this.online.setSuffix(Integer.toString(onlinePlayers));
        }
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();

        if (player.getWorld().getName().equals("world") && GameStates.isState(GameStates.STARTING)) {
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            player.setScoreboard(scoreboard);
            updateOnlinePlayers();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            updateTime(0);
            Bukkit.getScheduler().runTaskLater(sumo, this::updateOnlinePlayers, 0L);
        }
    }

}
