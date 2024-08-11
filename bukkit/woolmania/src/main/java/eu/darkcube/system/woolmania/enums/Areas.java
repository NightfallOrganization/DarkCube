/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.woolmania.manager.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;

public enum Areas {

    HALLPOOL1(HALLS, 25, 110, 19, 99, 68, -19),

    //-- Trennung

    HALL1(HALLS, -12, 178, 48, 142, 51, -61, "Halls");

    private final World world;
    private final double[] coordinate1;
    private final double[] coordinate2;
    private final int x1;
    private final int y1;
    private final int z1;
    private final int x2;
    private final int y2;
    private final int z2;
    private final @Nullable String name;

    Areas(World world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this(world, x1, y1, z1, x2, y2, z2, null);
    }

    Areas(World world, int x1, int y1, int z1, int x2, int y2, int z2, @Nullable String name) {
        this.world = world;
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.name = name;

        this.coordinate1 = new double[]{x1, y1, z1};
        this.coordinate2 = new double[]{x2, y2, z2};
    }

    public @Nullable String getName() {
        return name;
    }

    public boolean isWithinBounds(Location location) {
        if (location.getWorld() != world) {
            return false;
        }
        return TeleportLocations.isWithinBoundLocations(location, getFirstLocation(world), getSecondLocation(world));
    }

    public Location getFirstLocation(World world) {
        return new Location(world, getX1(), getY1(), getZ1());
    }

    public Location getSecondLocation(World world) {
        return new Location(world, getX2(), getY2(), getZ2());
    }

    public double[] getCoordinate1() {
        return this.coordinate1;
    }

    public double[] getCoordinate2() {
        return this.coordinate2;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getZ1() {
        return z1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    public int getZ2() {
        return z2;
    }

}
