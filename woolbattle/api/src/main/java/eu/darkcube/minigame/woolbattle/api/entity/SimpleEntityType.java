/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public record SimpleEntityType<T extends Entity>(@NotNull Class<T> entityTypeClass, @NotNull Key key, @NotNull EntityTypeTest<Entity, T> test) implements EntityType<T> {
    public SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key) {
        this(entityTypeClass, Key.key(key), EntityTypeTest.forClass(entityTypeClass));
        EntityType.REGISTRY.register(this.key, this);
    }

    @Override
    public @Nullable T tryCast(Entity obj) {
        return test.tryCast(obj);
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return test.getBaseClass();
    }
}
