/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ListenerRoundWalk extends BaseListener {

    private final Set<Location> requiredLocations;
    private final Map<Player, Set<Location>> playerLocationsVisited = new HashMap<>();
    private final Map<Player, Location> playerStartLocation = new HashMap<>();
    private final Map<Player, Integer> playerRounds = new HashMap<>();
    private final Map<Player, BukkitRunnable> playerResetTasks = new HashMap<>();
    private final Lobby plugin;

    public ListenerRoundWalk(Lobby plugin) {
        super();
        this.plugin = plugin;

        requiredLocations = new HashSet<>();
        requiredLocations.add(new Location(Bukkit.getWorlds().get(0), 0.5, 0, -8.5));
        requiredLocations.add(new Location(Bukkit.getWorlds().get(0), 9.5, 0, 0.5));
        requiredLocations.add(new Location(Bukkit.getWorlds().get(0), 0.5, 0, 9.5));
        requiredLocations.add(new Location(Bukkit.getWorlds().get(0), -8.5, 0, 0.5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (Location location : requiredLocations) {
            if (distanceSquared2D(player.getLocation(), location) <= 4) {
                playerLocationsVisited
                        .computeIfAbsent(player, k -> new HashSet<>())
                        .add(location);

                if (!playerStartLocation.containsKey(player)) {
                    playerStartLocation.put(player, player.getLocation());
                }
            }
        }

        if (playerLocationsVisited.containsKey(player) &&
                playerLocationsVisited.get(player).containsAll(requiredLocations) &&
                distanceSquared2D(player.getLocation(), playerStartLocation.get(player)) <= 4) {

            playerLocationsVisited.remove(player);

            int rounds = playerRounds.getOrDefault(player, 0);
            playerRounds.put(player, rounds + 1);
            player.sendMessage("§7Umrundet: §d" + (rounds + 1) + " §7mal");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 0.5f);

            // Reset timer
            if (playerResetTasks.containsKey(player)) {
                playerResetTasks.get(player).cancel();
            }

            BukkitRunnable resetTask = new BukkitRunnable() {
                @Override
                public void run() {
                    playerRounds.remove(player);
                    playerStartLocation.remove(player);
                    player.sendMessage("§7Deine Rundenzählung wurde zurückgesetzt!");
                    player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1.0f, 1.5f);
                }
            };

            resetTask.runTaskLater(plugin, 600);  // 30 seconds (600 ticks)
            playerResetTasks.put(player, resetTask);
        }
    }

    private double distanceSquared2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return dx * dx + dz * dz;
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerLocationsVisited.remove(player);
        playerStartLocation.remove(player);
        playerRounds.remove(player);
        if (playerResetTasks.containsKey(player)) {
            playerResetTasks.get(player).cancel();
            playerResetTasks.remove(player);
        }
    }

}
