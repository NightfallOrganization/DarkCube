/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class CloudNetMapIngameData implements MapIngameData {
    private final Map<String, Location> spawns = new HashMap<>();
    private World world;
    private int deathHeight;

    public World world() {
        return world;
    }

    public void world(World world) {
        this.world = world;
    }

    @Override
    public int deathHeight() {
        return deathHeight;
    }

    @Override
    public void deathHeight(int deathHeight) {
        this.deathHeight = deathHeight;
    }

    @Override
    public void spawn(String name, Location loc) {
        if (loc == null) spawns.remove(name);
        else {
            (loc = loc.clone()).setWorld(null);
            spawns.put(name, loc);
        }
    }

    @Override
    public Location spawn(String name) {
        Location loc = spawns.get(name);
        if (loc == null) return null;
        (loc = loc.clone()).setWorld(world);
        return loc;
    }
}
