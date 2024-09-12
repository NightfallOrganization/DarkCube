/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.common.entity;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

import eu.darkcube.minigame.woolbattle.api.entity.Arrow;
import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityImplementations;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.util.ProjectileUtil;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public abstract class CommonEntityImplementations implements EntityImplementations {
    private final CommonEntityGenerator generator = new CommonEntityGenerator();

    @Override
    public @NotNull Arrow spawnArrow(@NotNull Location location, float speed, float spread) {
        return shootArrow(null, location, speed, spread);
    }

    @Override
    public @NotNull Arrow shootArrow(@Nullable WBUser shooter, @NotNull Location location, float speed, float spread) {
        return shootProjectile(EntityType.ARROW, shooter, location, speed, spread);
    }

    @Override
    public <T extends Projectile> @NotNull T shootProjectile(@NotNull EntityType<T> type, @Nullable WBUser shooter, @NotNull Location location, float speed, float spread) {
        if (!type.directlyInstantiable()) {
            throw new IllegalArgumentException("Unable to create an entity of type " + type.key().asString() + ". This is an intermediate EntityType, create a wrapped entity type to use it!");
        }
        var velocity = ProjectileUtil.shootVelocity(ThreadLocalRandom.current(), location, speed, spread);
        return spawnProjectile0(type, (CommonWBUser) shooter, location, velocity, null);
    }

    public <T extends Entity, W extends Entity> @NotNull T createWrapped(@NotNull EntityType<T> type, @NotNull W wrapped) {
        if (type.entityTypeClass().isInstance(wrapped)) return type.entityTypeClass().cast(wrapped);
        return generator.createWrapped(type, wrapped);
    }

    @NotNull
    public Entity unwrap(@NotNull Entity entity) {
        while (entity instanceof GeneratedEntity generated) {
            entity = generated.unwrapGeneratedEntity();
        }
        return entity;
    }

    protected abstract <T extends Projectile> @NotNull T spawnProjectile0(@NotNull EntityType<T> type, @Nullable CommonWBUser shooter, @NotNull Location location, @NotNull Vector velocity, @Nullable Consumer<T> preSpawnCallback);
}
