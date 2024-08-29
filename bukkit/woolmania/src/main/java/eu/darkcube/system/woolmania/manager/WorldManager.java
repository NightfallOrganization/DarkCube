/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.woolmania.manager;

import eu.darkcube.system.woolmania.worldgen.VoidWorldGenerator;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

public class WorldManager {
    public static World SPAWN;
    public static World WORLD;
    public static World HALLS;

    public static void loadWorlds() {
        SPAWN = loadOrCreateWorldWithGenerator("Spawn", new VoidWorldGenerator());
        WORLD = loadOrCreateWorld("world");
        HALLS = loadOrCreateWorldWithGenerator("Halls", new VoidWorldGenerator());
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
