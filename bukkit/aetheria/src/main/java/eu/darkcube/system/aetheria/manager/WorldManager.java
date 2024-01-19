/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.aetheria.manager;

import org.bukkit.Bukkit;
import org.bukkit.World;

public class WorldManager {
    public static World MAINWORLD;
    public static World MONSTERWORLD;
    public static World WORLD;

    public static void loadWorlds() {
        MAINWORLD = Bukkit.getWorld("plotworld");
        MONSTERWORLD = Bukkit.getWorld("Beastrealm");
        WORLD = Bukkit.getWorld("world");
    }
}
