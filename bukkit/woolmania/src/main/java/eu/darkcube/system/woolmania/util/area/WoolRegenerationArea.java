/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import eu.darkcube.system.woolmania.enums.hall.Hall;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class WoolRegenerationArea {
    private final World world;
    private final int x1, y1, z1, x2, y2, z2;

    public WoolRegenerationArea(Hall hall) {
        this(hall.getPool().getWorld(), hall.getPool().getX1(), hall.getPool().getY1(), hall.getPool().getZ1(), hall.getPool().getX2(), hall.getPool().getY2(), hall.getPool().getZ2());
    }

    public WoolRegenerationArea(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
    }

    public void replaceAirAndLightWithWool(Material material) {
        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);
        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if ((block.getType() == Material.AIR) || (block.getType() == Material.LIGHT)) {
                        block.setType(material, false);
                    }
                }
            }
        }
    }

}
