/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.UnloadedLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class CloudNetMapIngameData implements MapIngameData {
    private final Map<String, UnloadedLocation> spawns = new HashMap<>();
    @GsonSerializer.DontSerialize
    private String worldName;
    @GsonSerializer.DontSerialize
    private World world;

    public String worldName() {
        return worldName;
    }

    public void worldName(String worldName) {
        this.worldName = worldName;
    }

    @Override public World world() {
        return world;
    }

    public void world(World world) {
        this.world = world;
    }

    @Override public void spawn(String name, Location loc) {
        if (loc == null) spawns.remove(name);
        else spawn(name, new UnloadedLocation(loc));
    }

    @Override public void spawn(String name, UnloadedLocation loc) {
        spawns.put(name, loc);
    }

    @Override public UnloadedLocation unloadedSpawn(String name) {
        return spawns.get(name);
    }

    @Override public Location spawn(String name) {
        UnloadedLocation loc = spawns.get(name);
        if (loc == null) return null;
        return loc.loaded();
    }

    @Override public String toString() {
        return "CloudNetMapIngameData{" + "spawns=" + spawns + ", worldName='" + worldName + '\'' + '}';
    }
}
