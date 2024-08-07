/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.map;

import java.util.Collection;

public interface MapManager {

    Map getMap(String name, MapSize mapSize);

    Map createMap(String name, MapSize mapSize);

    /**
     * @param mapSize the {@link MapSize}
     * @return a random map for the given MapSize that always gets returned by this method until the next reboot
     */
    Map defaultRandomPersistentMap(MapSize mapSize);

    Collection<? extends Map> getMaps();

    Collection<? extends Map> getMaps(MapSize mapSize);

    void deleteMap(Map map);

}
