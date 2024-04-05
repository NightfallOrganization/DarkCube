/*
 * Copyright (c) 2023. [DarkCube]
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
import java.util.*;

public class ListenerRoundWalk extends BaseListener {

    private final Set<Location> requiredLocations;
    private final Key keyRoundCount;
    private final Key keyVisitedLocations;
    private final Key keyStartLocation;

    public ListenerRoundWalk(Lobby plugin) {
        super();

        this.keyRoundCount = new Key(plugin, "round_count");
        this.keyVisitedLocations = new Key(plugin, "visited_locations");
        this.keyStartLocation = new Key(plugin, "start_location");

        var world = plugin.getDataManager().getSpawn().getWorld();

        requiredLocations = new HashSet<>();
        requiredLocations.add(new Location(world, 0.5, 0, -8.5));
        requiredLocations.add(new Location(world, 9.5, 0, 0.5));
        requiredLocations.add(new Location(world, 0.5, 0, 9.5));
        requiredLocations.add(new Location(world, -8.5, 0, 0.5));
    }

    @EventHandler public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        User user = UserAPI.instance().user(player.getUniqueId());

        for (Location location : requiredLocations) {
            if (distanceSquared2D(player.getLocation(), location) <= 5) {
                Set<Location> visitedLocations = user.metadata().get(keyVisitedLocations);
                if (visitedLocations == null) {
                    visitedLocations = new HashSet<>();
                    user.metadata().set(keyVisitedLocations, visitedLocations);
                }
                visitedLocations.add(location);

                if (!user.metadata().has(keyStartLocation)) {
                    user.metadata().set(keyStartLocation, player.getLocation());
                }
            }
        }

        if (user.metadata().has(keyVisitedLocations) && user
                .metadata()
                .<Set<Location>>get(keyVisitedLocations)
                .containsAll(requiredLocations) && distanceSquared2D(player.getLocation(), user.metadata().get(keyStartLocation)) <= 5) {

            user.metadata().remove(keyVisitedLocations);

            int rounds = user.persistentData().get(keyRoundCount, PersistentDataTypes.INTEGER, () -> 0) + 1;
            user.persistentData().set(keyRoundCount, PersistentDataTypes.INTEGER, rounds);
            user.sendMessage(Message.ROUNDS_COMPLETED, rounds);
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 0.5f);

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
