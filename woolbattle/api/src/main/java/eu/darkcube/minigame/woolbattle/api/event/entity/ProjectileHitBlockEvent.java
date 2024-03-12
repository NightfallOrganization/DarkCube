/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.world.Block;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class ProjectileHitBlockEvent extends ProjectileHitEvent {
    private final @NotNull Block block;

    public ProjectileHitBlockEvent(@NotNull Projectile entity, @NotNull Block block) {
        super(entity);
        this.block = block;
    }

    public Block block() {
        return block;
    }
}
