/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.enums;

import eu.darkcube.system.miners.manager.WorldManager;
import org.bukkit.Location;
import org.bukkit.World;

public enum TeleportLocations {
    LOBBY(WorldManager.LOBBY, -116.5, 45, -100.5, 0, 0),
    ARENA(WorldManager.ARENA, 0.5, 111, 0.5, -90, 0),
    MINE(WorldManager.MINE, 0.5, 111, 1000.5, -90, 0),

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
