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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class ListenerRoundWalk extends BaseListener {
    private final Lobby plugin;
    private final Key KEY_ROUND_COUNT;

    public ListenerRoundWalk(Lobby plugin) {
        this.plugin = plugin;
        this.KEY_ROUND_COUNT = new Key(plugin, "round_count");
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        User user = UserAPI.instance().user(event.getPlayer().getUniqueId());
        Player player = event.getPlayer();
        Location spawn = plugin.getDataManager().getSpawn();
        Location playerLocation = player.getLocation();

        if (!playerLocation.getWorld().equals(spawn.getWorld())) {
            return;
        }

        double distance = playerLocation.distance(spawn);

        if (distance >= 7 && distance <= 11) {

            int roundCount = user.persistentData().get(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, ()-> 0);

            roundCount++;

            user.persistentData().set(KEY_ROUND_COUNT, PersistentDataTypes.INTEGER, roundCount);

            player.sendMessage("Umrundet: " + roundCount + " mal");
        }
    }
}
