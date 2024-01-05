/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.sumo.Sumo;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class LobbyScoreboard implements Listener {

    private static final String ENTRY_TIME = "§1§f";
    private static final String ENTRY_MAP = "§2§b";

    private Scoreboard scoreboard;
    private Objective objective;
    private Team time;
    private Team map;

    public LobbyScoreboard(Sumo sumo) {
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

        time = scoreboard.registerNewTeam("lobby_time");
        time.setPrefix("§7Zeit: ");
        time.addEntry(ENTRY_TIME);
        objective.getScore(ENTRY_TIME).setScore(1);

        map = scoreboard.registerNewTeam("lobby_map");
        map.setPrefix("§7Map: ");
        map.addEntry(ENTRY_MAP);
        objective.getScore(ENTRY_MAP).setScore(2);

        updateTime(0);
    }

    public void updateTime(int time) {
        if (GameStates.isState(GameStates.STARTING)) {
            if (objective != null) {
                this.time.setSuffix(Integer.toString(time));
            }
        }
    }

    public void updateMap(String map) {
        if (GameStates.isState(GameStates.STARTING)) {
            if (objective != null) {
                this.map.setSuffix(map);
            }
        }
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            player.setScoreboard(scoreboard);
        }
    }
}
