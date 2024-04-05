/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.Key;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.math.BigInteger;
import java.util.*;

public class ListenerRoundWalk extends BaseListener {

    private final Set<Location> requiredLocations;
    private final Map<Player, Set<Location>> playerLocationsVisited = new HashMap<>();
    private final Map<Player, Location> playerStartLocation = new HashMap<>();
    private final Key KEY_ROUND_COUNT;
    private final Lobby plugin;

    public ListenerRoundWalk(Lobby plugin) {
        super();
        this.plugin = plugin;

        this.KEY_ROUND_COUNT = new Key(plugin, "round_count");

        var world = plugin.getDataManager().getSpawn().getWorld();

        requiredLocations = new HashSet<>();
        requiredLocations.add(new Location(world, 0.5, 0, -8.5));
        requiredLocations.add(new Location(world, 9.5, 0, 0.5));
        requiredLocations.add(new Location(world, 0.5, 0, 9.5));
        requiredLocations.add(new Location(world, -8.5, 0, 0.5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());

        for (Location location : requiredLocations) {
            if (distanceSquared2D(player.getLocation(), location) <= 5) {
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
                distanceSquared2D(player.getLocation(), playerStartLocation.get(player)) <= 5) {

            playerLocationsVisited.remove(player);

            int rounds = user.persistentData().get(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, () -> 0) + 1;
            user.persistentData().set(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, rounds);

            player.sendMessage("§7Umrundet: §d" + rounds + " §7mal");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 0.5f);

            BigInteger newcubes = user.cubes().add(BigInteger.valueOf(1));
            user.cubes(newcubes);
        }
    }

    @EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        Player player = event.getPlayer();

        playerLocationsVisited.remove(player);
        playerStartLocation.remove(player);
    }

    private double distanceSquared2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return dx * dx + dz * dz;
    }
}

