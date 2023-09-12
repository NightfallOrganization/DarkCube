/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LobbyTimer {

    private int timer = 60;
    private BukkitTask task = null;
    private final Main plugin;
    private int previousTimerValue = -1;
    private boolean running = false;

    public LobbyTimer(Main plugin) {
        this.plugin = plugin;
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
    }


    public int getRemainingTime() {
        return timer;
    }

    private class TimerRunnable extends BukkitRunnable {
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
            }
        }
    }

}
