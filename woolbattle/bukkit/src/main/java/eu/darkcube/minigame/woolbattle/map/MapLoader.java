/*
 * Copyright (c) 2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import java.util.concurrent.CompletableFuture;

public interface MapLoader {

    /**
     * Loads the given map. This downloads it from the templates and initializes the {@link MapIngameData} for the {@link Map}
     *
     * @return a future for the IO task
     */
    CompletableFuture<Void> loadMap(Map map);

    /**
     * Unloads the given map. This frees any resources it might occupy on the server like the Bukkit World, the Files, etc.
     * This will delete the {@link MapIngameData} for the map
     */
    void unloadMap(Map map);
}
