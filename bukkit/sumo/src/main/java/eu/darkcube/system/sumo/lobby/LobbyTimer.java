/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.lobby;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.team.MapTeamSpawns;
import eu.darkcube.system.sumo.team.TeamDistributor;
import eu.darkcube.system.sumo.team.TeamManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Team;

public class LobbyTimer {

    private int timer = 60;
    private BukkitTask task = null;
    private final Sumo plugin;
    private int previousTimerValue = -1;
    private boolean running = false;
    private final TeamManager teamManager;
    private final TeamDistributor teamDistributor;

    private GameState gameState = GameState.STOPPED;

    public enum GameState {
        STARTING, STARTED, STOPPED;
    }

    public LobbyTimer(Sumo plugin, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.teamDistributor = new TeamDistributor(teamManager);
    }

    public void setTimer(int seconds) {
        this.timer = seconds;

        // Falls ein Timer läuft, diesen beenden
        if (running) {
            task.cancel();
            running = false;
        }

        startTimer(); // Jetzt den Timer starten
    }

    public void startTimer() {
        // Falls bereits ein Timer läuft, diesen beenden
        if (running && task != null) {
            task.cancel();
        }

        task = new TimerRunnable().runTaskTimer(plugin, 20L, 20L);
        running = true;
        setGameState(GameState.STARTING);
    }

    public int getRemainingTime() {
        return timer;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    private class TimerRunnable extends BukkitRunnable {
        MapTeamSpawns mapTeamSpawns = new MapTeamSpawns();

        @Override
        public void run() {
            if (Bukkit.getOnlinePlayers().size() < 2) {
                timer = 60;
            } else {
                timer--;
            }

            if (timer != previousTimerValue) {
                // Pass the current LobbyTimer instance to the method
                LobbyScoreboardManager.updateScoreboardForAllPlayers(LobbyTimer.this);
                previousTimerValue = timer;
            }

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.setLevel(timer);
            }

            if (timer <= 0) {
                cancel();
                running = false;

                // Teleportiere Spieler zu ihren Team-Spawns
                for (Player player : Bukkit.getOnlinePlayers()) {
                    teamDistributor.distributeTeamsAfterTimer();

                    if (teamManager.isPlayerInTeam(player)) {
                        Team team = teamManager.playerTeams.get(player);
                        Location teamSpawn = mapTeamSpawns.getSpawnLocation("Origin", team.getName());
                        if (teamSpawn != null) {
                            player.teleport(teamSpawn);
                        } else {
                            System.out.println("Kein Teleportort für Team: " + team.getName());
                        }
                    } else {
                        player.teleport(new Location(Bukkit.getWorld("Origin"), 0, 110, 0));
                    }
                }
                setGameState(GameState.STARTED);
            }
        }
    }
}
