/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.ApiStatus;
import net.minestom.server.entity.Entity;
import net.minestom.server.thread.Acquirable;

@SuppressWarnings("UnstableApiUsage")
public class MinestomProjectile extends MinestomEntity implements Projectile {
    @ApiStatus.Experimental
    public MinestomProjectile(Acquirable<? extends Entity> entity, MinestomWoolBattle woolbattle) {
        super(entity, EntityType.PROJECTILE, woolbattle);
    }
}
