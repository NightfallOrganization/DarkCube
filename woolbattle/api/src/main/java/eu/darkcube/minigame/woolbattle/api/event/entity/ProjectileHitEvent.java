/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.system.event.RecursiveEvent;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ProjectileHitEvent implements ProjectileEvent, RecursiveEvent {
    private final @NotNull Projectile entity;

    public ProjectileHitEvent(@NotNull Projectile entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull Projectile entity() {
        return entity;
    }
}
