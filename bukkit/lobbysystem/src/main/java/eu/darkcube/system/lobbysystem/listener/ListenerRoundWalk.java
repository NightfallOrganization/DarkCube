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
import java.util.ArrayList;
import java.util.List;

public class ListenerRoundWalk extends BaseListener {
    private final Key KEY_ROUND_COUNT;
    private List<Location> points;

    public ListenerRoundWalk(Lobby plugin) {
        this.KEY_ROUND_COUNT = new Key(plugin, "round_count");
        this.points = new ArrayList<>();

        var world = plugin.getDataManager().getSpawn().getWorld();

        points.add(new Location(world, 0.5, 0, -8.5));
        points.add(new Location(world, 9.5, 0, 0.5));
        points.add(new Location(world, 0.5, 0, 9.5));
        points.add(new Location(world, -8.5, 0, 0.5));
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        for (Location point : points) {
            if (Math.abs(to.getX() - point.getX()) <= 4 && Math.abs(to.getZ() - point.getZ()) <= 4) {

                if (!(Math.abs(from.getX() - point.getX()) <= 4 && Math.abs(from.getZ() - point.getZ()) <= 4)) {

                    User user = UserAPI.instance().user(player.getUniqueId());

                    int roundCount = user.persistentData().get(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, () -> 0);

                    roundCount++;

                    user.persistentData().set(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, roundCount);

                    player.sendMessage("§7Umrundet: §d" + roundCount + " §7mal");
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 50f, 0.5f);
                    break;
                }
            }
        }
    }
}

