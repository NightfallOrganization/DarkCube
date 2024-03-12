/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.util;

import java.util.Map;

import eu.darkcube.system.util.data.BasicMetaDataStorage;
import eu.darkcube.system.util.data.MetaDataStorage;

public class CommonRedirectMetaDataStorage<Key> implements MetaDataStorage {

    private final Map<Key, BasicMetaDataStorage> map;
    private final Key key;

    public CommonRedirectMetaDataStorage(Map<Key, BasicMetaDataStorage> map, Key key) {
        this.map = map;
        this.key = key;
    }

    @Override
    public void set(eu.darkcube.system.util.data.Key key, Object value) {
        synchronized (map) {
            var blockMeta = map.get(this.key);
            if (blockMeta == null) {
                blockMeta = new BasicMetaDataStorage();
                map.put(this.key, blockMeta);
            }
            blockMeta.set(key, value);
        }
    }

    @Override
    public <T> T get(eu.darkcube.system.util.data.Key key) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return null;
        return blockMeta.get(key);
    }

    @Override
    public <T> T getOr(eu.darkcube.system.util.data.Key key, T orElse) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return orElse;
        return blockMeta.getOr(key, orElse);
    }

    @Override
    public boolean has(eu.darkcube.system.util.data.Key key) {
        var blockMeta = map.get(this.key);
        if (blockMeta == null) return false;
        return blockMeta.has(key);
    }

    @Override
    public <T> T remove(eu.darkcube.system.util.data.Key key) {
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
