/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import eu.darkcube.system.sumo.Sumo;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class StartingTimer implements Listener {
    private int timer = 15;
    private BukkitTask task = null;
    private final Sumo plugin; // Ersetze YourPlugin mit deinem Plugin-Klassennamen
    private boolean running = false;
    private LobbyScoreboard lobbyScoreboard;
    private Respawn respawn;

    public StartingTimer(Sumo plugin, LobbyScoreboard lobbyScoreboard, Respawn respawn) {
        this.plugin = plugin;
        this.lobbyScoreboard = lobbyScoreboard;
        this.respawn = respawn;
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
        // Füge hier eventuell weitere Aktionen hinzu, die durchgeführt werden sollen, wenn der Timer gestoppt wird
    }

    private void startTimer() {
        if (running && task != null) {
            task.cancel();
        }

        timer = 15; // Timer zurücksetzen
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

            if (timer <= 3) {
                playSoundToAllPlayers(Sound.WOOD_CLICK, 1.0f, 1.5f);
                Bukkit.broadcastMessage("§7Spiel startet in §b" + timer);
            }

            timer--;

            if (timer <= 0) {
                cancel();
                running = false;

                GameStates.setState(GameStates.PLAYING);

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    respawn.teleportPlayerRandomly(player);
                }

            }
        }
    }

    private void playSoundToAllPlayers(Sound sound, float volume, float pitch) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }
}
