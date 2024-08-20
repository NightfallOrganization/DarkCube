/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;

import eu.darkcube.system.woolmania.manager.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;

public enum TeleportLocations {
    SPAWN(WorldManager.SPAWN, 15.5, 199, -3.5, 45, 0),
    HALL1(HALLS, 0.5, 111, 0.5, -90, 0),
    HALL2(HALLS, 0.5, 111, 1000.5, -90, 0),
    HALL3(HALLS, 0.5, 111, 2000.5, -90, 0),

    ;

    private final World world;
    private final double[] coordinates;
    private final float yaw;
    private final float pitch;
    private final double x;
    private final double y;
    private final double z;

    TeleportLocations(World world, double x, double y, double z, float yaw, float pitch) {
        this.world = world;
        this.coordinates = new double[]{x, y, z};
        this.yaw = yaw;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
    }

    public World getWorld() {
        return world;
    }

    public double[] getCoordinates() {
        return this.coordinates;
    }

    public double getPositonX() {
        return this.x;
    }

    public double getPositonY() {
        return this.y;
    }

    public double getPositonZ() {
        return this.z;
    }

    public float getPositionYaw() {
        return this.yaw;
    }

    public float getPositionPitch() {
        return this.pitch;
    }

    public Location getLocation() {
        return new Location(world, getPositonX(), getPositonY(), getPositonZ(), getPositionYaw(), getPositionPitch());
    }
}
