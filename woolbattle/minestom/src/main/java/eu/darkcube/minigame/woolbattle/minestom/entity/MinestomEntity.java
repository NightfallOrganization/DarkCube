/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;
import net.minestom.server.entity.Entity;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.thread.Acquired;

@SuppressWarnings("UnstableApiUsage")
public class MinestomEntity implements DefaultMinestomEntity {
    private final Acquirable<? extends net.minestom.server.entity.Entity> entity;
    private final EntityType<?> type;
    private final MinestomWoolBattle woolbattle;

    @ApiStatus.Experimental
    public MinestomEntity(Acquirable<? extends net.minestom.server.entity.Entity> entity, MinestomWoolBattle woolbattle) {
        this(entity, EntityType.ENTITY, woolbattle);
    }

    @ApiStatus.Experimental
    protected MinestomEntity(Acquirable<? extends net.minestom.server.entity.Entity> entity, EntityType<?> type, MinestomWoolBattle woolbattle) {
        this.entity = entity;
        this.type = type;
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return woolbattle.entityMeta(this);
    }

    @Override
    public @NotNull EntityType<?> type() {
        return type;
    }

    @Override
    public void remove() {
        woolbattle.removed(this);
    }

    public Acquirable<? extends net.minestom.server.entity.Entity> entity() {
        return entity;
    }

    @Override
    public Acquired<? extends Entity> lock() {
        return entity.lock();
    }

    @Override
    public @NotNull MinestomWoolBattle woolbattle() {
        return woolbattle;
    }
}
