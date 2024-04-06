/*
 * Copyright (c) 2023-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.lobbysystem.listener;

import eu.darkcube.system.lobbysystem.Lobby;
import eu.darkcube.system.lobbysystem.util.Message;
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
import java.util.HashSet;
import java.util.Set;

public class ListenerLobbyRoundWalk extends BaseListener {

    private final Set<Location> requiredLobbyLocations;
    private final Key keyLobbyRoundCount;
    private final Key keyLobbyVisitedLocations;
    private final Key keyLobbyStartLocation;

    public ListenerLobbyRoundWalk(Lobby plugin) {
        super();

        this.keyLobbyRoundCount = new Key(plugin, "lobby_round_count");
        this.keyLobbyVisitedLocations = new Key(plugin, "lobby_visited_locations");
        this.keyLobbyStartLocation = new Key(plugin, "lobby_start_location");

        var world = plugin.getDataManager().getSpawn().getWorld();

        requiredLobbyLocations = new HashSet<>();
        requiredLobbyLocations.add(new Location(world, -1.5, 0, -17));
        requiredLobbyLocations.add(new Location(world, 92, 0, -39.5));
        requiredLobbyLocations.add(new Location(world, 156, 0, -120.5));
        requiredLobbyLocations.add(new Location(world, 179.5, 0, -231.5));
        requiredLobbyLocations.add(new Location(world, 76.5, 0, -304.5));
        requiredLobbyLocations.add(new Location(world, -38.5, 0, -301.5));
        requiredLobbyLocations.add(new Location(world, -96.5, 0, -199.5));
        requiredLobbyLocations.add(new Location(world, -110.5, 0, -121.5));
        requiredLobbyLocations.add(new Location(world, -63.5, 0, -62.5));
    }

    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());

        for (Location location : requiredLobbyLocations) {
            if (distanceSquared2D(player.getLocation(), location) <= 300) {
                Set<Location> visitedLocations = user.metadata().get(keyLobbyVisitedLocations);
                if (visitedLocations == null) {
                    visitedLocations = new HashSet<>();
                    user.metadata().set(keyLobbyVisitedLocations, visitedLocations);
                }
                visitedLocations.add(location);

                if (!user.metadata().has(keyLobbyStartLocation)) {
                    user.metadata().set(keyLobbyStartLocation, player.getLocation());
                }
            }
        }

        if (user.metadata().has(keyLobbyVisitedLocations) && user
                .metadata()
                .<Set<Location>>get(keyLobbyVisitedLocations)
                .containsAll(requiredLobbyLocations) && distanceSquared2D(player.getLocation(), user.metadata().get(keyLobbyStartLocation)) <= 300) {

            user.metadata().remove(keyLobbyVisitedLocations);

            int rounds = user.persistentData().get(keyLobbyRoundCount, PersistentDataTypes.INTEGER, () -> 0) + 1;
            user.persistentData().set(keyLobbyRoundCount, PersistentDataTypes.INTEGER, rounds);

            user.sendMessage(Message.ROUNDS_COMPLETED, rounds);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 0.5f);

            BigInteger newCubes = user.cubes().add(BigInteger.valueOf(20));
            user.cubes(newCubes);
        }
    }

    private double distanceSquared2D(Location loc1, Location loc2) {
        double dx = loc1.getX() - loc2.getX();
        double dz = loc1.getZ() - loc2.getZ();
        return dx * dx + dz * dz;
    }
}
