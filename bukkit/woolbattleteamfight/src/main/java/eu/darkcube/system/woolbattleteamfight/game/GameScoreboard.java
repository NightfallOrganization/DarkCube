/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.event.Listener;

public class GameScoreboard implements Listener {

    private final Scoreboard scoreboard;
    private final Objective objective;

    public GameScoreboard() {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();

        // Hier ändern wir die Art und Weise, wie das Objective registriert wird.
        this.objective = scoreboard.registerNewObjective("gameScoreboard", "dummy");
        objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&5Dark&dCube&8.&5eu"));

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setupScoreboard();
    }

    private void setupScoreboard() {
        addEntry("&6» &f☾&c✧&f☽ &6« &aGrün", 3);
        addEntry("&6» &f☾&c✧&f☽ &6« &9Blau", 2);
        addEntry("&6» &f☾&c✧&f☽ &6« &cRot", 1);
        addEntry("&6» &f☾&c✧&f☽ &6« &5Violett", 0);
    }

    private void addEntry(String text, int score) {
        Score scoreEntry = objective.getScore(ChatColor.translateAlternateColorCodes('&', text));
        scoreEntry.setScore(score);
    }

    public void displayToPlayer(Player player) {
        if (!player.getWorld().getName().equalsIgnoreCase("world")) {
            player.setScoreboard(scoreboard);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        displayToPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("world")) {
            displayToPlayer(event.getPlayer());
        }
    }

}
