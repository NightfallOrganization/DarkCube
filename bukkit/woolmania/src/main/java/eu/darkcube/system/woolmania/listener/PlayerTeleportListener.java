/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.listener;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerTeleportListener implements Listener {

    @EventHandler
    private void playerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
        Location location = event.getTo();
        new BukkitRunnable() {
            @Override
            public void run() {
                boolean foundHall = false;
                for (Halls hall : Halls.values()) {
                    if (hall.getHallArea().isWithinBounds(location)) {
                        woolManiaPlayer.updateHallByWorldChange(hall);
                        foundHall = true;
                        break;
                    }
                }

                if (!foundHall) {
                    woolManiaPlayer.updateHallByWorldChange(null);
                }
            }
        }.runTask(WoolMania.getInstance());
    }

}
