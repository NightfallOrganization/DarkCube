/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.other.GameStates;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Ending {
    private int timer = 10;
    private BukkitTask task = null;
    private final Sumo plugin;

    public Ending(Sumo plugin) {
        this.plugin = plugin;
    }

    public void execute(ChatColor teamColor) {
        GameStates.setState(GameStates.ENDING);
        teleportPlayersToSpawn();
        startTimer();
    }

    private void teleportPlayersToSpawn() {
        World world = Bukkit.getWorld("world");
        if (world != null) {
            Location targetLocation = new Location(world, 0.5, 101, 0.5);
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(targetLocation);
            }
        } else {
            System.out.println("Welt 'world' nicht gefunden!");
        }
    }

    private void startTimer() {
        if (task != null) {
            task.cancel();
        }

        timer = 10;
        task = new EndingTimerRunnable().runTaskTimer(plugin, 20L, 20L);
    }

    private class EndingTimerRunnable extends BukkitRunnable {
        @Override
        public void run() {

            if (timer <= 3) {
                playSoundToAllPlayers();
                Bukkit.broadcastMessage("§7Spiel endet in §b" + timer);
            }

            timer--;

            if (timer <= 0) {
                cancel();
                onTimerEnd();
            }
        }
    }

    private void onTimerEnd() {
        Bukkit.shutdown();
    }

    private void playSoundToAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, (float) 1.0, (float) 1.5);
        }
    }
}
