/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.util;

import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class UnloadedLocation {
    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public String world;
    @GsonSerializer.DontSerialize
    private Location loaded = null;

    public UnloadedLocation(Location l) {
        this(l.getX(), l.getY(), l.getZ(), l.getYaw(), l.getPitch(), l.getWorld().getName());
    }

    public UnloadedLocation(double x, double y, double z, float yaw, float pitch, String world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.world = world;
    }

    public @Nullable Location loaded() {
        World loadedWorld;
        if (world == null) {
            loadedWorld = null;
            loaded = null;
        } else loadedWorld = Bukkit.getWorld(world);
        if (loadedWorld == null) return loaded = null;
        if (loaded == null) loaded = new Location(loadedWorld, x, y, z, yaw, pitch);
        return loaded.clone();
    }
}
