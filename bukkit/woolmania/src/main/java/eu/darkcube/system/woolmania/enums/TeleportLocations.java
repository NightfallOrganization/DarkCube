/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.enums;

import static eu.darkcube.system.woolmania.manager.WorldManager.HALLS;
import static eu.darkcube.system.woolmania.manager.WorldManager.MAINWORLD;

import org.bukkit.Location;
import org.bukkit.World;

public enum TeleportLocations {
    SPAWN(MAINWORLD, 631.5, 121, 467, -90, 0),
    HALL1(HALLS, 0.5, 111, 0.5, -90, 0)

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
        this.coordinates = new double[] {x, y, z};
        this.yaw = yaw;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
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

    public static boolean isWithinBoundLocations(Location location, Location corner1, Location corner2) {

        double x1 = Math.min(corner1.getX(), corner2.getX());
        double y1 = Math.min(corner1.getY(), corner2.getY());
        double z1 = Math.min(corner1.getZ(), corner2.getZ());
        double x2 = Math.max(corner1.getX(), corner2.getX());
        double y2 = Math.max(corner1.getY(), corner2.getY());
        double z2 = Math.max(corner1.getZ(), corner2.getZ());

        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return x >= x1 && x <= x2 && y >= y1 && y <= y2 && z >= z1 && z <= z2;
    }

}
