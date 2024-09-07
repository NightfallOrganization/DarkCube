/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.miners.manager;

import eu.darkcube.system.miners.worldgen.defaultgen.VoidWorldGenerator;
import eu.darkcube.system.miners.worldgen.minegen.MineGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

public class WorldManager {
    public static World LOBBY;
    public static World WORLD;
    public static World ARENA;
    public static World MINE;

    public static void loadWorlds() {
        LOBBY = loadOrCreateWorldWithGenerator("MinersWartelobby", new VoidWorldGenerator());
        LOBBY.setViewDistance(4);
        LOBBY.setSimulationDistance(4);
        ARENA = loadOrCreateWorldWithGenerator("Arena", new VoidWorldGenerator());
        MINE = loadOrCreateWorldWithGenerator("Mine", new MineGenerator());
        MINE.setViewDistance(4);
        MINE.setSimulationDistance(4);
    }

    private static World loadOrCreateWorld(String worldName) {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            world = Bukkit.createWorld(new WorldCreator(worldName));
        }
        return world;
    }

    private static World loadOrCreateWorldWithGenerator(String worldName, ChunkGenerator generator) {
        WorldCreator creator = new WorldCreator(worldName);
        creator.generator(generator);
        return Bukkit.createWorld(creator);
    }

}
