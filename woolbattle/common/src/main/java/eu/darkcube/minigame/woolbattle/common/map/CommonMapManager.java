/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;

import eu.cloudnetservice.driver.database.Database;
import eu.cloudnetservice.driver.database.DatabaseProvider;
import eu.cloudnetservice.driver.inject.InjectionLayer;
import eu.darkcube.minigame.woolbattle.api.map.Map;
import eu.darkcube.minigame.woolbattle.api.map.MapManager;
import eu.darkcube.minigame.woolbattle.api.map.MapSize;
import eu.darkcube.minigame.woolbattle.common.CommonWoolBattleApi;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnmodifiableView;

public class CommonMapManager implements MapManager {
    private static final ConcurrentMap<String, CommonMap> EMPTY = new ConcurrentHashMap<>(0);
    private final Database database;
    private final ConcurrentMap<MapSize, ConcurrentMap<String, CommonMap>> maps = new ConcurrentHashMap<>();

    public CommonMapManager(CommonWoolBattleApi woolbattle) {
        database = InjectionLayer.boot().instance(DatabaseProvider.class).database("woolbattle_maps" + woolbattle.databaseNameSuffixMaps());
    }

    @Override
    public @Nullable CommonMap map(@NotNull String name, @NotNull MapSize mapSize) {
        return mapsRO(mapSize).get(name);
    }

    @Override
    public @NotNull CommonMap createMap(@NotNull String name, @NotNull MapSize mapSize) {
        var mapRef = new AtomicReference<CommonMap>();
        maps.compute(mapSize, (k, v) -> {
            if (v == null) v = new ConcurrentHashMap<>();
            mapRef.set(v.computeIfAbsent(name, n -> {
                var map = new CommonMap(n, mapSize);
                database.insert(databaseKey(map), map.toDocument());
                return map;
            }));
            return v;
        });
        return mapRef.get();
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
        maps.compute(map.size(), (k, v) -> {
            if (v == null) return null;
            v.remove(map.name());
            if (v.isEmpty()) return null;
            return v;
        });
    }

    private String databaseKey(CommonMap map) {
        return map.name() + "-" + map.size();
    }

    private @UnmodifiableView ConcurrentMap<String, CommonMap> mapsRO(MapSize mapSize) {
        return maps.getOrDefault(mapSize, EMPTY);
    }
}
