/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.game;

import eu.darkcube.system.sumo.game.items.LifeManager;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.*;

public class GameScoreboard implements Listener {

    private final Scoreboard scoreboard;
    private final Objective objective;
    private LifeManager lifeManager;
    private TeamManager teamManager;

    public GameScoreboard(LifeManager lifeManager, TeamManager teamManager) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager.getNewScoreboard();
        this.lifeManager = lifeManager;
        this.teamManager = teamManager;

        int blackLives = teamManager.getTotalLivesOfTeam("Black", lifeManager);
        int whiteLives = teamManager.getTotalLivesOfTeam("White", lifeManager);

        // Hier ändern wir die Art und Weise, wie das Objective registriert wird.
        this.objective = scoreboard.registerNewObjective("gameScoreboard", "dummy");
        objective.setDisplayName(ChatColor.WHITE + "« " + ChatColor.DARK_GRAY + "Dark" + ChatColor.GRAY + "Cube" + ChatColor.WHITE + "." + ChatColor.DARK_GRAY + "eu" + ChatColor.WHITE + " »");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        setupScoreboard(blackLives, whiteLives);
    }

    public void updateTeamLives(TeamManager teamManager, LifeManager lifeManager) {
        int blackLives = teamManager.getTotalLivesOfTeam("Black", lifeManager);
        int whiteLives = teamManager.getTotalLivesOfTeam("White", lifeManager);

        setupScoreboard(blackLives, whiteLives);
    }

    private void setupScoreboard(int blackLives, int whiteLives) {
        resetScoreboard();
        addEntry(" ", 2);
        addEntry("&7× &f" + blackLives + " &7× &8Schwarz", 1);
        addEntry("&7× &f" + whiteLives + " &7× &fWeiß", 0);
    }

    private void resetScoreboard() {
        for (String entry : objective.getScoreboard().getEntries()) {
            objective.getScoreboard().resetScores(entry);
        }
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
