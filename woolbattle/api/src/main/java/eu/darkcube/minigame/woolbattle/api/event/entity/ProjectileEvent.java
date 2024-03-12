/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.event.entity;

import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface ProjectileEvent extends EntityEvent {
    @Override
    @NotNull Projectile entity();
}
