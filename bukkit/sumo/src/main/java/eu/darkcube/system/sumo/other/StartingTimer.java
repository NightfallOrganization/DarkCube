/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.executions.EquipPlayer;
import eu.darkcube.system.sumo.executions.RandomTeam;
import eu.darkcube.system.sumo.executions.Respawn;
import eu.darkcube.system.sumo.manager.TeamManager;
import eu.darkcube.system.sumo.prefix.PrefixManager;
import eu.darkcube.system.sumo.scoreboards.LobbyScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class StartingTimer implements Listener {
    private int timer = 15;
    private BukkitTask task = null;
    private final Sumo plugin; // Ersetze YourPlugin mit deinem Plugin-Klassennamen
    private boolean running = false;
    private LobbyScoreboard lobbyScoreboard;
    private Respawn respawn;
    private EquipPlayer equipPlayer;
    private TeamManager teamManager;
    private RandomTeam randomTeam;
    private PrefixManager prefixManager;

    public StartingTimer(Sumo plugin, LobbyScoreboard lobbyScoreboard, Respawn respawn, EquipPlayer equipPlayer, TeamManager teamManager, RandomTeam randomTeam, PrefixManager prefixManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.lobbyScoreboard = lobbyScoreboard;
        this.equipPlayer = equipPlayer;
        this.respawn = respawn;
        this.randomTeam = randomTeam;
        this.prefixManager = prefixManager;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (GameStates.isState(GameStates.STARTING)) {
            if (Bukkit.getOnlinePlayers().size() >= 2 && !running) {
                startTimer();
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() - 1 < 2 && running) {
            stopTimer();
        }
    }

    private void stopTimer() {
        if (task != null) {
            task.cancel();
        }
        running = false;
        Bukkit.broadcastMessage("§7Der §bTimer §7wurde gestoppt");
    }

    public void zeroTimer() {
        if (running && task != null) {
            task.cancel();
        }

        GameStates.setState(GameStates.PLAYING);
        Set<UUID> playerIDs = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            randomTeam.balanceTeams(playerIDs);
            equipPlayer.equipPlayerIfInTeam(player);
            respawn.teleportPlayerRandomly(player);
        }
    }

    public void startTimer() {
        if (running && task != null) {
            task.cancel();
        }

        timer = 15;
        task = new TimerRunnable().runTaskTimer(plugin, 20L, 20L);
        running = true;
    }

    public void setTimer(int newTime) {
        if (newTime > 0) {
            this.timer = newTime;
        }
    }

    private class TimerRunnable extends BukkitRunnable {
        @Override
        public void run() {
            lobbyScoreboard.updateTime(timer);

            if (GameStates.isState(GameStates.PLAYING) || (GameStates.isState(GameStates.ENDING))) {
                cancel();
            }

            if (timer <= 3) {
                playSoundToAllPlayers();
                Bukkit.broadcastMessage("§7Spiel startet in §b" + timer);
            }

            timer--;

            if (timer <= 0) {
                cancel();
                running = false;

                GameStates.setState(GameStates.PLAYING);
                Set<UUID> playerIDs = Bukkit.getServer().getOnlinePlayers().stream().map(Player::getUniqueId).collect(Collectors.toSet());

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    randomTeam.balanceTeams(playerIDs);
                    equipPlayer.equipPlayerIfInTeam(player);
                    respawn.teleportPlayerRandomly(player);
                    prefixManager.setPlayerPrefix(player);
                }

            }
        }
    }

    private void playSoundToAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, (float) 1.0, (float) 1.5);
        }
    }
}
