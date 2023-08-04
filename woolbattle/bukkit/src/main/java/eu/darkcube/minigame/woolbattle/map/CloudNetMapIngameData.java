/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import eu.darkcube.minigame.woolbattle.WoolBattle;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;
import eu.darkcube.minigame.woolbattle.util.UnloadedLocation;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class CloudNetMapIngameData implements MapIngameData {
    private final Map<String, UnloadedLocation> spawns = new HashMap<>();
    private CloudNetMapLoader loader = ((CloudNetMapLoader) WoolBattle.instance().mapLoader());
    private String worldName;
    @GsonSerializer.DontSerialize
    private World world;

    public String worldName() {
        return worldName;
    }

    public void worldName(String worldName) {
        this.worldName = worldName;
    }

    public World world() {
        return world;
    }

    public void world(World world) {
        this.world = world;
    }

    @Override
    public void spawn(String name, Location loc) {
        if (loc == null) spawns.remove(name);
        else {
            UnloadedLocation l = new UnloadedLocation(loc);
            spawns.put(name, l);
        }
    }

    @Override
    public Location spawn(String name) {
        UnloadedLocation loc = spawns.get(name);
        if (loc == null) return null;
        return loc.loaded();
    }
}