/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class LobbyScoreboardManager implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            setScoreboard(event.getPlayer(), Main.getInstance().getLobbyTimer());
        }
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer().getWorld().getName().equals("world")) {
            setScoreboard(event.getPlayer(), Main.getInstance().getLobbyTimer());
        }
    }

    public static void updateScoreboardForAllPlayers(LobbyTimer timer) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getWorld().getName().equals("world")) {
                setScoreboard(player, timer);
            }
        }
    }

    private static void setScoreboard(Player player, LobbyTimer timer) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("lobby", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(ChatColor.DARK_PURPLE + "Dark" + ChatColor.LIGHT_PURPLE + "Cube" + ChatColor.DARK_GRAY + "." + ChatColor.DARK_PURPLE + "eu");

        Score epGlitch = objective.getScore(ChatColor.GRAY + "EP-Glitch: " + ChatColor.RED + "Aus");
        epGlitch.setScore(5);

        // Use the timer's getRemainingTime method
        Score zeit = objective.getScore(ChatColor.GRAY + "Zeit: " + ChatColor.GOLD + timer.getRemainingTime());
        zeit.setScore(4);

        Score map = objective.getScore(ChatColor.GRAY + "Map: " + ChatColor.GOLD + "WBT-1");
        map.setScore(3);
        Score benoetigt = objective.getScore(ChatColor.GRAY + "Benötigt: " + ChatColor.GOLD + "2");
        benoetigt.setScore(2);
        Score online = objective.getScore(ChatColor.GRAY + "Online: " + ChatColor.GOLD + Bukkit.getOnlinePlayers().size());
        online.setScore(1);

        player.setScoreboard(scoreboard);
    }
}

