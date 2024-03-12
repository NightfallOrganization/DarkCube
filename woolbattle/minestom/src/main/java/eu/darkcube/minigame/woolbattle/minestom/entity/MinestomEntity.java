/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.minestom.WoolBattleMinestom;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.util.data.MetaDataStorage;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.thread.Acquirable;

@SuppressWarnings("UnstableApiUsage")
public class MinestomEntity implements Entity {
    private final Acquirable<net.minestom.server.entity.Entity> entity;
    private final WoolBattleMinestom woolbattle;

    @ApiStatus.Experimental
    public MinestomEntity(Acquirable<net.minestom.server.entity.Entity> entity, WoolBattleMinestom woolbattle) {
        this.entity = entity;
        this.woolbattle = woolbattle;
    }

    @Override
    public @NotNull Location location() {
        var lock = entity.lock();
        var entity = lock.get();
        var pos = entity.getPosition();
        var instance = entity.getInstance();
        lock.unlock();
        var world = woolbattle.worlds().get(instance);
        var position = new Position.Directed.Simple(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
        return new Location(world, position);
    }

    @Override
    public void velocity(@NotNull Vector velocity) {
        entity.sync(entity -> entity.setVelocity(new Vec(velocity.x(), velocity.y(), velocity.z())));
    }

    @Override
    public @NotNull MetaDataStorage metadata() {
        return woolbattle.entityMeta(this);
    }

    @Override
    public void remove() {
        woolbattle.removed(this);
    }

    public Acquirable<net.minestom.server.entity.Entity> entity() {
        return entity;
    }
}
