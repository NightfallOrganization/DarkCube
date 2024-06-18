/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package building.oneblock.manager;

import building.oneblock.voidgen.VoidGenerator;
import net.kyori.adventure.util.TriState;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

public class WorldManager {
    public static World WORLD;
    public static World SPAWN;

    public static void loadWorlds() {
        WORLD = loadOrCreateWorldWithGenerator("world", new VoidGenerator());
        SPAWN = loadOrCreateWorldWithGenerator("spawn", new VoidGenerator());
    }

//    private static World loadOrCreateWorld(String worldName) {
//        World world = Bukkit.getWorld(worldName);
//        if (world == null) {
//            world = Bukkit.createWorld(new WorldCreator(worldName));
//        }
//        return world;
//    }

    private static World loadOrCreateWorldWithGenerator(String worldName, ChunkGenerator generator) {
        WorldCreator creator = new WorldCreator(worldName).generator(generator).keepSpawnLoaded(TriState.FALSE);
        return Bukkit.createWorld(creator);
    }

    public static World createPlayerSpecificVoidWorld(String playerName) {
        int worldIndex = 1;
        String playerWorldName;
        World playerWorld;

        do {
            playerWorldName = playerName + "-" + worldIndex;
            playerWorld = Bukkit.getWorld(playerWorldName);
            if (playerWorld == null) {

                playerWorld = loadOrCreateWorldWithGenerator(playerWorldName, new VoidGenerator());
                break;
            }

            worldIndex++;

        } while (true);

        return playerWorld;
    }

}

