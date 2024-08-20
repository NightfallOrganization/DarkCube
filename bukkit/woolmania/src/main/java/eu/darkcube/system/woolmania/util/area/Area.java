/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import org.bukkit.Location;
import org.bukkit.World;

public interface Area {
    int getX1();

    int getY1();

    int getZ1();

    int getX2();

    int getY2();

    int getZ2();

    World getWorld();

    default Location getFirstLocation(World world) {
        return new Location(world, getX1(), getY1(), getZ1());
    }

    default Location getSecondLocation(World world) {
        return new Location(world, getX2(), getY2(), getZ2());
    }

    default boolean isWithinBounds(Location location) {
        if (location.getWorld() != getWorld()) {
            return false;
        }
        return isWithinBoundLocations(location, getFirstLocation(getWorld()), getSecondLocation(getWorld()));
    }

    static boolean isWithinBoundLocations(Location location, Location corner1, Location corner2) {
        double x = location.getX();
        double minX = Math.min(corner1.getX(), corner2.getX());
        if (x < minX) return false;
        double maxX = Math.max(corner1.getX(), corner2.getX()) + 1;
        if (x >= maxX) return false;

        double y = location.getY();
        double minY = Math.min(corner1.getY(), corner2.getY());
        if (y < minY) return false;
        double maxY = Math.max(corner1.getY(), corner2.getY()) + 1;
        if (y >= maxY) return false;

        double z = location.getZ();
        double minZ = Math.min(corner1.getZ(), corner2.getZ());
        if (z < minZ) return false;
        double maxZ = Math.max(corner1.getZ(), corner2.getZ()) + 1;
        return !(z >= maxZ);
    }
}
