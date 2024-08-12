/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.Hall;
import eu.darkcube.system.woolmania.util.WoolManiaPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class AreaTeleporters {

    public AreaTeleporters() {
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        WoolManiaPlayer p = WoolMania.getStaticPlayer(event.getPlayer());
        Hall hall = p.getHall();
        if (hall != null) {
            if(hall.getTeleportArea().isWithinBounds(event.getPlayer().getLocation())) {
                // open inventory
            }
        }
    }

}
