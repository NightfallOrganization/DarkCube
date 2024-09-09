/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.system.event.Cancellable;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ProjectileHitEntityEvent extends ProjectileHitEvent implements Cancellable {
    private final @NotNull Entity target;
    private final @NotNull Position collisionPosition;
    private boolean cancelled = false;

    public ProjectileHitEntityEvent(@NotNull Projectile entity, @NotNull Entity target, @NotNull Position collisionPosition) {
        super(entity);
        this.target = target;
        this.collisionPosition = collisionPosition;
    }

    public @NotNull Entity target() {
        return target;
    }

    public @NotNull Position collisionPosition() {
        return collisionPosition;
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
