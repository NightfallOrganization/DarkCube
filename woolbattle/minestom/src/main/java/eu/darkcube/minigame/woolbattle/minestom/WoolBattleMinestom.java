/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import eu.darkcube.minigame.woolbattle.common.entity.CommonEntityMetaDataStorage;
import eu.darkcube.minigame.woolbattle.minestom.entity.MinestomEntity;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.util.data.BasicMetaDataStorage;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.thread.Acquirable;

public class WoolBattleMinestom {
    private final MinestomWoolBattleApi api;
    private final Map<Instance, MinestomWorld> worlds = new ConcurrentHashMap<>();
    private final Map<Entity, MinestomEntity> entities = new ConcurrentHashMap<>();
    private final Map<String, BasicMetaDataStorage> entityMetas = new ConcurrentHashMap<>();

    public WoolBattleMinestom() {
        api = new MinestomWoolBattleApi(this);
    }

    public void start() {
        api.lobbySystemLink().enable();
    }

    public void stop() {
        api.lobbySystemLink().disable();
    }

    public Map<Instance, MinestomWorld> worlds() {
        return worlds;
    }

    public CommonEntityMetaDataStorage entityMeta(MinestomEntity entity) {
        return new CommonEntityMetaDataStorage(entityMetas, String.valueOf(entity.entity().unwrap().getEntityId()));
    }

    public MinestomEntity entity(Acquirable<Entity> entity) {
        return entities.computeIfAbsent(entity.unwrap(), k -> new MinestomEntity(k.getAcquirable(), this));
    }

    public void removed(MinestomEntity entity) {
        entities.remove(entity.entity().unwrap());
        entityMeta(entity).clear();
    }
}
