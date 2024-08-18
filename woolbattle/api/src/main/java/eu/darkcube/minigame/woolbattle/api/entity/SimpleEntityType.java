/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record SimpleEntityType<T extends Entity>(@NotNull Class<T> entityTypeClass, @NotNull Key key) implements EntityType<T> {
    public SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key) {
        this(entityTypeClass, Key.key(key));
    }
}
