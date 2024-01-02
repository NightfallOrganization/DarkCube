/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.ruler;

import org.bukkit.World;
import org.bukkit.Bukkit;

import java.util.Random;
import java.util.List;
import java.util.Arrays;

public class MainRuler {
    private World activeWorld;
    private static final List<String> AVAILABLE_MAPS = Arrays.asList("Origin", "Atlas", "Demonic");

    public MainRuler() {
        // Zufällige Welt beim Start setzen
        String worldName = AVAILABLE_MAPS.get(new Random().nextInt(AVAILABLE_MAPS.size()));
        setActiveWorld(Bukkit.getWorld(worldName));
    }

    public void setActiveWorld(World world) {
        this.activeWorld = world;
    }

    public World getActiveWorld() {
        return this.activeWorld;
    }

}
