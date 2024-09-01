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
    HALL4(HALLS, 0.5, 111, 3000.5, -90, 0),
    HALL5(HALLS, 0.5, 111, 4000.5, -90, 0),
    HALL6(HALLS, 0.5, 111, 5000.5, -90, 0),
    HALL7(HALLS, 0.5, 111, 6000.5, -90, 0),
    HALL8(HALLS, 0.5, 111, 7000.5, -90, 0),
    HALL9(HALLS, 0.5, 111, 8000.5, -90, 0),
    HALL10(HALLS, 0.5, 111, 9000.5, -90, 0),
    HALL11(HALLS, 0.5, 111, 10000.5, -90, 0),
    HALL12(HALLS, 0.5, 111, 11000.5, -90, 0),
    HALL13(HALLS, 0.5, 111, 12000.5, -90, 0),
    HALL14(HALLS, 0.5, 111, 13000.5, -90, 0),
    HALL15(HALLS, 0.5, 111, 14000.5, -90, 0),
    HALL16(HALLS, 0.5, 111, 15000.5, -90, 0),
    HALL17(HALLS, 0.5, 111, 16000.5, -90, 0),
    HALL18(HALLS, 0.5, 111, 17000.5, -90, 0),
    HALL19(HALLS, 0.5, 111, 18000.5, -90, 0),
    HALL20(HALLS, 0.5, 111, 19000.5, -90, 0),

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
