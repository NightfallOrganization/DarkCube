/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.map;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapIngameData;
import eu.darkcube.minigame.woolbattle.api.map.MapManager;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;
import eu.darkcube.system.server.item.ItemBuilder;

public class CommonMapManager implements MapManager {
    private static final ConcurrentMap<String, CommonMap> EMPTY = new ConcurrentHashMap<>(0);
    private final Database mapsDatabase;
    private final Database mapDataDatabase;
    private final CommonWoolBattleApi api;
    private final ConcurrentMap<MapSize, ConcurrentMap<String, CommonMap>> maps = new ConcurrentHashMap<>();

    public CommonMapManager(CommonWoolBattleApi api) {
        this.api = api;
        var databaseProvider = InjectionLayer.boot().instance(DatabaseProvider.class);
        mapsDatabase = databaseProvider.database("woolbattle_maps_" + api.platformName());
        mapDataDatabase = databaseProvider.database("woolbattle_map_data");
        for (var entry : mapsDatabase.entries().entrySet()) {
            var document = entry.getValue();
            var map = CommonMap.fromDocument(this, document);
            maps.computeIfAbsent(map.size(), _ -> new ConcurrentHashMap<>()).put(map.name(), map);
        }
        init();
    }

    public void init() {
        for (var map : maps()) {
            if (!Files.exists(map.schematicPath())) {
                api.woolbattle().logger().error("No schematic file for Map {}-{}", map.name(), map.size());
            }
        }
    }

    public CommonWoolBattleApi api() {
        return api;
    }

    @Override
    public @Nullable CommonMap map(@NotNull String name, @NotNull MapSize mapSize) {
        return mapsRO(mapSize).get(name);
    }

    @Override
    public @NotNull CommonMap createMap(@NotNull String name, @NotNull MapSize mapSize) {
        var mapRef = new AtomicReference<CommonMap>();
        maps.compute(mapSize, (_, v) -> {
            if (v == null) v = new ConcurrentHashMap<>();
            mapRef.set(v.computeIfAbsent(name, n -> {
                var map = new CommonMap(this, n, mapSize, ItemBuilder.item(api.materialProvider().grassBlock()));
                mapsDatabase.insert(databaseKey(map), map.toDocument());
                return map;
            }));
            return v;
        });
        return mapRef.get();
    }

    void save(CommonMap map) {
        mapsDatabase.insert(databaseKey(map), map.toDocument());
    }

    @Override
    public @NotNull @UnmodifiableView Collection<CommonMap> maps() {
        var l = new ArrayList<CommonMap>();
        for (var value : maps.values()) l.addAll(value.values());
        return l;
    }

    @Override
    public @NotNull @UnmodifiableView Collection<CommonMap> maps(@NotNull MapSize mapSize) {
        return Collections.unmodifiableCollection(mapsRO(mapSize).values());
    }

    @Override
    public void deleteMap(@NotNull Map map) {
        maps.compute(map.size(), (_, v) -> {
            if (v == null) return null;
            v.remove(map.name());
            if (v.isEmpty()) return null;
            return v;
        });
    }

    @Override
    public @NotNull CommonMapIngameData loadIngameData(@NotNull Map map) {
        if (!(map instanceof CommonMap m)) throw new IllegalArgumentException("Invalid Map");
        var ingameData = new CommonMapIngameData(m);
        var document = mapDataDatabase.get(databaseKey(m));
        if (document != null) {
            ingameData.readFromDocument(document);
        }
        return ingameData;
    }

    @Override
    public void saveIngameData(@NotNull MapIngameData ingameData) {
        if (!(ingameData instanceof CommonMapIngameData data)) throw new IllegalArgumentException("Invalid MapIngameData");
        mapDataDatabase.insert(databaseKey(data.map()), data.toDocument());
    }

    private String databaseKey(CommonMap map) {
        return map.name() + "-" + map.size();
    }

    private @UnmodifiableView ConcurrentMap<String, CommonMap> mapsRO(MapSize mapSize) {
        return maps.getOrDefault(mapSize, EMPTY);
    }
}
