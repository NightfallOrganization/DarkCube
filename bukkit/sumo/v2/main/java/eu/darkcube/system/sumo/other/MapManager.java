/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.sumo.other;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.event.Listener;

public class MapManager implements Listener {

    private final String WORLD_NAME = "Origin";

    public MapManager() {
    }

    public void loadWorlds(){
        loadWorld(WORLD_NAME);
    }

    private void loadWorld(String name) {
        if(Bukkit.getWorld(name)==null){
            new WorldCreator(name).createWorld();
        }
    }

}
