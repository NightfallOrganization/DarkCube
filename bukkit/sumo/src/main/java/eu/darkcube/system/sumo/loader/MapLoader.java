/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.loader;

import eu.darkcube.system.sumo.voidgen.VoidGenerator;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;

public class MapLoader implements Listener {

    private static final String[] WORLDS = {"Origin", "Atlas", "Demonic", "world"};

    public MapLoader() {
    }

    public void loadWorlds() {
        for (String worldName : WORLDS) {
            loadWorld(worldName);
        }
    }

    private void loadWorld(String name) {
        if (Bukkit.getWorld(name) == null) {
            Bukkit.getServer().getLogger().info("Loading world: " + name);
            WorldCreator creator = new WorldCreator(name);
            creator.generator(new VoidGenerator());
            creator.createWorld();
        }
    }

}
