/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import eu.darkcube.system.woolmania.WoolMania;
import eu.darkcube.system.woolmania.enums.hall.Halls;
import eu.darkcube.system.woolmania.util.player.WoolManiaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class WoolAreas {

    public static void regenerateWoolAreas() {
        for (Halls hall : Halls.values()) {
            var woolRegenerationArea = new WoolRegenerationArea(hall);

            for (Player player : Bukkit.getOnlinePlayers()) {
                WoolManiaPlayer woolManiaPlayer = WoolMania.getStaticPlayer(player);
                if (woolManiaPlayer.getHall() != hall) continue;
                if (hall.getPool().isWithinBounds(player.getLocation())) {
                    woolManiaPlayer.teleportSyncTo(hall);
                }
            }

            woolRegenerationArea.replaceAirAndLight(hall.getWoolEntries());
        }
    }

    public static void clearWoolAreas() {
        for (Halls hall : Halls.values()) {
            var woolRegenerationArea = new WoolRegenerationArea(hall);
            woolRegenerationArea.removeBlocks();
        }
    }
}
