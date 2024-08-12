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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RegenerateWoolAreas {

    public static void regenerateWoolAreas(Hall hall) {

        WoolRegenerationArea woolRegenerationArea = new WoolRegenerationArea(hall);

        for (Player player : Bukkit.getOnlinePlayers()) {
            WoolManiaPlayer p = WoolMania.getStaticPlayer(player);
            if (p.getHall() != hall) continue;
            if (hall.getPool().isWithinBounds(player.getLocation())) {
                p.teleportSyncTo(hall);
            }
        }

        woolRegenerationArea.replaceAirAndLightWithWool(hall.getWool());
    }

    public static void regenerateWoolAreas() {
        for (Hall hall : Hall.values()) {
            regenerateWoolAreas(hall);
        }
    }

}
