/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

public class LobbyScoreboard implements Listener {

    private Scoreboard scoreboard;
    private Objective objective;

    public LobbyScoreboard() {
        createScoreboard();
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugins()[0]); // Registrieren des Listeners, Annahme: Hauptplugin als erstes Plugin
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

        updateTime(0);
    }

    public void updateTime(int time) {
        if (GameStates.isState(GameStates.STARTING)) {
            if (objective != null) {
                for (String entry : scoreboard.getEntries()) {
                    scoreboard.resetScores(entry);
                }

                Score score = objective.getScore("§7Zeit: §f" + time);
                score.setScore(0);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            Player player = event.getPlayer();
            player.setScoreboard(scoreboard);
        }
    }
}
