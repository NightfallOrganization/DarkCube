/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager.player;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

import static building.oneblock.manager.WorldManager.SPAWN;

public class RespawnManager implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase(SPAWN.getName())) {
            Location respawnLocation = new Location(event.getPlayer().getWorld(), 0, 100, 0);
            event.setRespawnLocation(respawnLocation);
        }
    }
}
