/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import java.util.Collection;

public interface MapManager {

    Map getMap(String name, MapSize mapSize);

    Map createMap(String name, MapSize mapSize);

    Collection<? extends Map> getMaps();

    Collection<? extends Map> getMaps(MapSize mapSize);

    void deleteMap(Map map);

}
