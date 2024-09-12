/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.MinestomProjectileImpl;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import net.minestom.server.thread.Acquirable;
import net.minestom.server.thread.Acquired;

@SuppressWarnings("UnstableApiUsage")
public class MinestomProjectile extends MinestomEntity implements Projectile {
    private @Nullable CommonWBUser shooter;

    @ApiStatus.Experimental
    public MinestomProjectile(Acquirable<? extends MinestomProjectileImpl> entity, MinestomWoolBattle woolbattle, @Nullable CommonWBUser shooter) {
        super(entity, EntityType.PROJECTILE, woolbattle);
        this.shooter = shooter;
    }

    @Override
    public @Nullable CommonWBUser shooter() {
        return this.shooter;
    }

    @Override
    public void shooter(@Nullable WBUser shooter) {
        this.shooter = (CommonWBUser) shooter;
    }

    @Override
    public boolean frozen() {
        var lock = lock();
        var frozen = lock.get().frozen();
        lock.unlock();
        return frozen;
    }

    @Override
    public void freeze() {
        var lock = lock();
        lock.get().freeze();
        lock.unlock();
    }

    @Override
    public void unfreeze() {
        var lock = lock();
        lock.get().unfreeze();
        lock.unlock();
    }

    @Override
    public @NotNull Acquired<? extends MinestomProjectileImpl> lock() {
        return (Acquired<? extends MinestomProjectileImpl>) super.lock();
    }
}
