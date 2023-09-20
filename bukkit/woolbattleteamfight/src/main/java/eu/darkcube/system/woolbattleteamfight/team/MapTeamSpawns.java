/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolbattleteamfight.team;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class MapTeamSpawns {
    private Map<String, Map<String, Location>> worldTeamSpawnLocations = new HashMap<>();

    public MapTeamSpawns() {
        // Für die Welt WBT-1
        Map<String, Location> wbt1Spawns = new HashMap<>();
        wbt1Spawns.put("Red", new Location(Bukkit.getWorld("WBT-1"), 0.5, 121, -57.5, 0, 0));
        wbt1Spawns.put("Blue", new Location(Bukkit.getWorld("WBT-1"), -57.5, 121, 0.5, -90, 0));
        wbt1Spawns.put("Violet", new Location(Bukkit.getWorld("WBT-1"), 58.5, 121, 0.5, 90, 0));
        wbt1Spawns.put("Green", new Location(Bukkit.getWorld("WBT-1"), 0.5, 121, 58.5, -180, 0));

        worldTeamSpawnLocations.put("WBT-1", wbt1Spawns);
    }

    public Location getSpawnLocation(String worldName, String teamName) {
        if (worldTeamSpawnLocations.containsKey(worldName)) {
            return worldTeamSpawnLocations.get(worldName).get(teamName);
        }
        return null;
    }
}
