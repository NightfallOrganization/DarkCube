/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.util.area;

import eu.darkcube.system.woolmania.enums.TeleportLocations;
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
        return TeleportLocations.isWithinBoundLocations(location, getFirstLocation(getWorld()), getSecondLocation(getWorld()));
    }
}
