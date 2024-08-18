/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Registry;
import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public interface EntityType<T extends Entity> {
    Registry<EntityType<?>> REGISTRY = Registry.create();
    EntityType<WBUser> PLAYER = new SimpleEntityType<>(WBUser.class, "woolbattle:player");
    EntityType<Entity> ENTITY = new SimpleEntityType<>(Entity.class, "woolbattle:entity");
    EntityType<Projectile> PROJECTILE = new SimpleEntityType<>(Projectile.class, "woolbattle:projectile");

    @NotNull
    Class<T> entityTypeClass();

    @NotNull
    Key key();
}
