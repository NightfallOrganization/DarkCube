/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.executions;

import eu.darkcube.system.sumo.Sumo;
import eu.darkcube.system.sumo.other.GameStates;
import eu.darkcube.system.sumo.other.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
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

    public void execute() {
        GameStates.setState(GameStates.ENDING);
        teleportPlayersToSpawn();
        startTimer();
        plugin.getLobbySystemLink().updateLobbyLink();
        System.out.println("Ending execute ausgeführt!!!");
    }

    private void teleportPlayersToSpawn() {
        World world = Bukkit.getWorld("world");
        Location targetLocation = new Location(world, 0.5, 101, 0.5);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(targetLocation);
            System.out.println(player.getName() + " wurde teleportiert!!!");
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

            if (GameStates.isState(GameStates.PLAYING) || (GameStates.isState(GameStates.STARTING))) {
                cancel();
            }

            if (timer <= 3) {
                playSoundToAllPlayers();

                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    User user = UserAPI.instance().user(onlinePlayer.getUniqueId());
                    user.sendMessage(Message.BC_GAME_END, timer);
                }
            }

            timer--;

            if (timer <= 0) {
                cancel();
                onTimerEnd();
            }
        }
    }

    private void onTimerEnd() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer("");
        }
        Bukkit.shutdown();
    }

    private void playSoundToAllPlayers() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.WOOD_CLICK, (float) 1.0, (float) 1.5);
        }
    }
}
