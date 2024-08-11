/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import static eu.darkcube.system.woolmania.enums.Areas.HALLPOOL1;
import static eu.darkcube.system.woolmania.enums.TeleportLocations.HALL1;
import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import org.bukkit.Material;

public class RegenerateWoolAreas {

    public static void regenerateWoolAreas() {
        TeleportArea teleportArea = new TeleportArea(HALLS, HALLPOOL1.getX1(),HALLPOOL1.getY1(),HALLPOOL1.getZ1(),HALLPOOL1.getX2(),HALLPOOL1.getY2(),HALLPOOL1.getZ2());
        WoolRegenerationArea woolRegenerationArea = new WoolRegenerationArea(HALLS,HALLPOOL1.getX1(),HALLPOOL1.getY1(),HALLPOOL1.getZ1(),HALLPOOL1.getX2(),HALLPOOL1.getY2(),HALLPOOL1.getZ2());

        teleportArea.teleportPlayersTo(HALL1.getLocation(HALLS));
        woolRegenerationArea.replaceAirWithWool(Material.WHITE_WOOL);
    }

}
