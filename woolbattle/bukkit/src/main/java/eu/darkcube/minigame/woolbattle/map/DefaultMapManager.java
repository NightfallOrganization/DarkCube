/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.database.Database;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class DefaultMapManager implements MapManager {

    final Database database =
            CloudNetDriver.getInstance().getDatabaseProvider().getDatabase("woolbattle_maps");
    private final java.util.Map<String, Map> maps = new HashMap<>();

    public DefaultMapManager() {
        for (JsonDocument document : database.documents()) {
            String mapJson = document.toJson();
            Map map = GsonSerializer.gson.fromJson(mapJson, Map.class);
            this.maps.put(map.getName(), map);
        }
    }

    @Override
    public Map getMap(String name) {
        return maps.get(name);
    }

    @Override
    public Map createMap(String name, MapSize mapSize) {
        Map map = getMap(name);
        if (map != null)
            return map;
        DefaultMap dmap = new DefaultMap(name, mapSize);
        map = dmap;
        maps.put(name, map);
        database.insert(name, dmap.toDocument());
        return map;
    }

    @Override
    public Collection<? extends Map> getMaps() {
        return Collections.unmodifiableCollection(maps.values());
    }

    @Override
    public void deleteMap(Map map) {
        maps.remove(map.getName());
        database.delete(map.getName());
    }
}
