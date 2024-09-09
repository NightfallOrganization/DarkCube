/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import java.util.function.Consumer;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.ItemEntity;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.entity.CommonEntityImplementations;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityImpl;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityMappings;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.MinestomProjectileImpl;
import eu.darkcube.minigame.woolbattle.minestom.util.MinestomUtil;
import eu.darkcube.minigame.woolbattle.minestom.world.MinestomWorld;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.item.ItemStack;
import net.minestom.server.thread.Acquirable;

public class MinestomEntityImplementations extends CommonEntityImplementations {
    private final MinestomWoolBattle woolbattle;
    private final EntityMappings mappings;

    public MinestomEntityImplementations(MinestomWoolBattle woolbattle) {
        this.woolbattle = woolbattle;
        this.mappings = new EntityMappings(woolbattle);
    }

    @Override
    protected <T extends Projectile> @NotNull T spawnProjectile0(@NotNull eu.darkcube.minigame.woolbattle.api.entity.EntityType<T> type, @Nullable CommonWBUser shooter, @NotNull Location location, @NotNull Vector velocity, float speed, float spread, @Nullable Consumer<T> preSpawnCallback) {
        var pair = mappings.create(type);
        var entity = pair.first();
        var custom = new MinestomProjectile((Acquirable<? extends MinestomProjectileImpl>) entity.acquirable(), woolbattle, shooter);
        return configure(pair, custom, type, location, velocity, preSpawnCallback);
    }

    @Override
    public <T extends ItemEntity> @NotNull T spawnItem(@NotNull Location location, @NotNull Vector velocity, @NotNull ItemBuilder item, @Nullable Consumer<T> preSpawnCallback) {
        var pair = mappings.create(EntityType.ITEM, item.<ItemStack>build());
        var entity = pair.first();
        var custom = new MinestomItemEntity(entity.acquirable(), woolbattle);
        return configure(pair, custom, (EntityType<T>) EntityType.ITEM, location, velocity, preSpawnCallback);
    }

    @Override
    public <T extends Entity> @NotNull T spawn(@NotNull eu.darkcube.minigame.woolbattle.api.entity.EntityType<T> type, @NotNull Location location, @NotNull Vector velocity, @Nullable Consumer<T> preSpawnCallback) {
        var pair = mappings.create(type);
        var entity = pair.first();
        var custom = new MinestomItemEntity(entity.acquirable(), woolbattle);
        return configure(pair, custom, type, location, velocity, preSpawnCallback);
    }

    private <T extends Entity> T configure(Pair<net.minestom.server.entity.Entity, EntityImpl> pair, Entity custom, EntityType<T> type, Location location, Vector velocity, Consumer<T> preSpawnCallback) {
        var entity = pair.first();
        var wrapped = createWrapped(type, custom);
        pair.second().handle(wrapped);
        if (preSpawnCallback != null) {
            preSpawnCallback.accept(wrapped);
        }
        var instance = ((MinestomWorld) location.world()).instance();
        var lock = entity.acquirable().lock();
        entity.setInstance(instance, MinestomUtil.toPos(location));
        entity.setVelocity(MinestomUtil.toVec(velocity));
        lock.unlock();
        return wrapped;
    }
}
