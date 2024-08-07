/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Message;
import eu.darkcube.system.userapi.User;
import eu.darkcube.system.userapi.UserAPI;
import eu.darkcube.system.util.data.PersistentDataTypes;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerSpawnRoundWalk extends BaseListener {

    private final Set<Location> requiredSpawnLocations;
    private final Key keySpawnRoundCount;
    private final Key keySpawnVisitedLocations;
    private final Key keySpawnStartLocation;

    public ListenerSpawnRoundWalk(Lobby plugin) {
        super();

        this.keySpawnRoundCount = Key.key(plugin, "spawn_round_count");
        this.keySpawnVisitedLocations = Key.key(plugin, "spawn_visited_locations");
        this.keySpawnStartLocation = Key.key(plugin, "spawn_start_location");

        var world = plugin.getDataManager().getSpawn().getWorld();

        requiredSpawnLocations = new HashSet<>();
        requiredSpawnLocations.add(new Location(world, 0.5, 0, -8.5));
        requiredSpawnLocations.add(new Location(world, 9.5, 0, 0.5));
        requiredSpawnLocations.add(new Location(world, 0.5, 0, 9.5));
        requiredSpawnLocations.add(new Location(world, -8.5, 0, 0.5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());

        for (Location location : requiredSpawnLocations) {
            if (distanceSquared2D(player.getLocation(), location) <= 5) {
                Set<Location> visitedLocations = user.metadata().get(keySpawnVisitedLocations);
                if (visitedLocations == null) {
                    visitedLocations = new HashSet<>();
                    user.metadata().set(keySpawnVisitedLocations, visitedLocations);
                }
                visitedLocations.add(location);

                if (!user.metadata().has(keySpawnStartLocation)) {
                    user.metadata().set(keySpawnStartLocation, player.getLocation());
                }
            }
        }

        if (user.metadata().has(keySpawnVisitedLocations) && user.metadata().<Set<Location>>get(keySpawnVisitedLocations).containsAll(requiredSpawnLocations) && distanceSquared2D(player.getLocation(), user.metadata().get(keySpawnStartLocation)) <= 5) {

            user.metadata().remove(keySpawnVisitedLocations);

            int rounds = user.persistentData().get(keySpawnRoundCount, PersistentDataTypes.INTEGER, () -> 0) + 1;
            user.persistentData().set(keySpawnRoundCount, PersistentDataTypes.INTEGER, rounds);

            user.sendMessage(Message.ROUNDS_COMPLETED, rounds);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 0.5f);

            BigInteger newCubes = user.cubes().add(BigInteger.valueOf(1));
            user.cubes(newCubes);
        }
    }

    private double distanceSquared2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return dx * dx + dz * dz;
    }
}
