/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.thread.Acquired;

public interface DefaultMinestomEntity extends Entity {
    Acquired<? extends net.minestom.server.entity.Entity> lock();

    @NotNull
    MinestomWoolBattle woolbattle();

    @Override
    default @NotNull Location location() {
        var lock = lock();
        var entity = lock.get();
        var pos = entity.getPosition();
        var instance = entity.getInstance();
        lock.unlock();
        var world = woolbattle().worlds().get(instance);
        var position = new Position.Directed.Simple(pos.x(), pos.y(), pos.z(), pos.yaw(), pos.pitch());
        return new Location(world, position);
    }

    @Override
    default boolean isAlive() {
        var lock = lock();
        var entity = lock.get();
        var alive = !entity.isActive() || !(entity instanceof LivingEntity l) || !l.isDead();
        lock.unlock();
        return alive;
    }

    @Override
    default void velocity(@NotNull Vector velocity) {
        var lock = lock();
        lock.get().setVelocity(new Vec(velocity.x(), velocity.y(), velocity.z()));
        lock.unlock();
    }

    @Override
    default @NotNull BoundingBox boundingBox() {
        var lock = lock();
        var e = lock.get();
        var b = e.getBoundingBox();
        b = b.withOffset(e.getPosition().sub(b.width() / 2, 0, b.depth() / 2));
        lock.unlock();
        return new BoundingBox(b.minX(), b.minY(), b.minZ(), b.maxX(), b.maxY(), b.maxZ());
    }

    @Override
    default @NotNull Component getName() {
        var lock = lock();
        var e = lock.get();
        var customName = e.getCustomName();

        if (customName == null) {
            var type = e.getEntityType();
            var id = type.key();
            customName = net.kyori.adventure.text.Component.translatable("entity." + id.namespace() + "." + id.value());
        }
        lock.unlock();
        return KyoriAdventureSupport.adventureSupport().convert(customName);
    }
}
