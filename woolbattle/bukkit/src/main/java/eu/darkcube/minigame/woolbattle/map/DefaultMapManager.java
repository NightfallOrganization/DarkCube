/*
 * Copyright (c) 2022-2023. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */
package eu.darkcube.minigame.woolbattle.map;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.document.Document;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.util.GsonSerializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class DefaultMapManager implements MapManager {

    final Database database = InjectionLayer.boot().instance(DatabaseProvider.class).database("woolbattle_maps");
    private final java.util.Map<MapSize, java.util.Map<String, Map>> maps = new HashMap<>();

    public DefaultMapManager() {
        for (java.util.Map.Entry<String, Document> entry : database.entries().entrySet()) {
            Document document = entry.getValue();
            String mapJson = document.serializeToString();
            Map map = GsonSerializer.gson.fromJson(mapJson, Map.class);
            maps(map.size()).put(map.getName(), map);
        }
    }

    @Override public Map getMap(String name, MapSize mapSize) {
        return maps(mapSize).get(name);
    }

    @Override public Map createMap(String name, MapSize mapSize) {
        Map map = getMap(name, mapSize);
        if (map != null) return map;
        DefaultMap defaultMap = new DefaultMap(name, mapSize);
        map = defaultMap;
        maps(map.size()).put(name, map);
        database.insert(databaseKey(map), defaultMap.toDocument());
        return map;
    }

    private java.util.Map<String, Map> maps(MapSize size) {
        return maps.computeIfAbsent(size, s -> new HashMap<>());
    }

    @Override public Collection<? extends Map> getMaps() {
        Collection<Map> m = new ArrayList<>();
        for (java.util.Map<String, Map> value : maps.values()) {
            m.addAll(value.values());
        }
        return Collections.unmodifiableCollection(m);
    }

    @Override public Collection<? extends Map> getMaps(MapSize mapSize) {
        return Collections.unmodifiableCollection(maps(mapSize).values());
    }

    @Override public void deleteMap(Map map) {
        maps(map.size()).remove(map.getName());
        database.delete(databaseKey(map));
    }

    private String databaseKey(Map map) {
        return map.getName() + "-" + map.size();
    }

    void save(DefaultMap map) {
        database.insert(databaseKey(map), map.toDocument());
    }
}
