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
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomUtil;
import eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.thread.Acquired;

public interface DefaultMinestomEntity extends Entity {
    @NotNull
    Acquired<? extends net.minestom.server.entity.Entity> lock();

    @NotNull
    MinestomWoolBattle woolbattle();

    @Override
    default Location location() {
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
    default Location eyeLocation() {
        var lock = lock();
        var location = location();
        var eyeLocation = location.add(0, eyeHeight(), 0);
        lock.unlock();
        return eyeLocation;
    }

    @Override
    default double eyeHeight() {
        var lock = lock();
        var eyeHeight = lock.get().getEyeHeight();
        lock.unlock();
        return eyeHeight;
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
        lock.get().setVelocity(MinestomUtil.toVelocity(velocity));
        lock.unlock();
    }

    @Override
    @UnknownNullability
    default Vector velocity() {
        var lock = lock();
        var vec = lock.get().getVelocity();
        lock.unlock();
        return MinestomUtil.toVelocity(vec);
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
