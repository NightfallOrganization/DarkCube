/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import java.util.Map;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonRedirectMetaDataStorage<KeyType> implements MetaDataStorage {

    private final Map<KeyType, BasicMetaDataStorage> map;
    private final KeyType key;

    public CommonRedirectMetaDataStorage(Map<KeyType, BasicMetaDataStorage> map, KeyType key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void set(Key key, Object value) {
        synchronized (map) {
            var blockMeta = map.computeIfAbsent(this.key, k -> new BasicMetaDataStorage());
            blockMeta.set(key, value);
        }
    }

    @Override
    public <T> @Nullable T get(Key key) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return null;
        return blockMeta.get(key);
    }

    @Override
    public <T> T getOr(Key key, T orElse) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return orElse;
        return blockMeta.getOr(key, orElse);
    }

    @Override
    public boolean has(Key key) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return false;
        return blockMeta.has(key);
    }

    @Override
    public <T> T remove(Key key) {
        synchronized (map) {
            var blockMeta = map.get(this.key);
            if (blockMeta == null) return null;
            var data = blockMeta.<T>remove(key);
            if (blockMeta.data.isEmpty()) {
                map.remove(this.key);
            }
            return data;
        }
    }

    @Override
    public void clear() {
        synchronized (map) {
            map.remove(this.key);
        }
    }
}
