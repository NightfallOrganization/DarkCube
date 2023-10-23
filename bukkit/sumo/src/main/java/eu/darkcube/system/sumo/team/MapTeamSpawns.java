/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.team;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MapTeamSpawns {
    private Map<String, Map<String, Location>> worldTeamSpawnLocations = new HashMap<>();
    private Random random = new Random();

    public MapTeamSpawns() {
        // Für die Welt WBT-1
        Map<String, Location> originSpawns = new HashMap<>();
        originSpawns.put("Black", getRandomLocationAroundOrigin());
        originSpawns.put("Rot", getRandomLocationAroundOrigin());

        worldTeamSpawnLocations.put("Origin", originSpawns);
    }

    public Location getSpawnLocation(String worldName, String teamName) {
        if (worldTeamSpawnLocations.containsKey(worldName)) {
            return worldTeamSpawnLocations.get(worldName).get(teamName);
        }
        return null;
    }

    private Location getRandomLocationAroundOrigin() {
        int x = random.nextInt(17) - 8; // Zufälliger Wert zwischen -8 und +8
        int z = random.nextInt(17) - 8; // Zufälliger Wert zwischen -8 und +8
        return new Location(Bukkit.getWorld("Origin"), x + 0.5, 110, z + 0.5);
    }
}
