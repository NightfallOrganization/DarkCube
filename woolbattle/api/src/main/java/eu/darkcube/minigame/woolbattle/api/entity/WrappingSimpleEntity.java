/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.net.kyori.adventure.text.Component;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.UnknownNullability;
import eu.darkcube.system.util.data.MetaDataStorage;

public interface WrappingSimpleEntity<T extends Entity> extends Entity {
    @NotNull
    Entity handle();

    @Override
    @NotNull
    EntityType<? extends T> type();

    @Override
    default @UnknownNullability Location location() {
        return handle().location();
    }

    @Override
    default void velocity(@NotNull Vector velocity) {
        handle().velocity(velocity);
    }

    @Override
    default @NotNull MetaDataStorage metadata() {
        return handle().metadata();
    }

    @Override
    default boolean isAlive() {
        return handle().isAlive();
    }

    @Override
    default void remove() {
        handle().remove();
    }

    @Override
    default @NotNull Component getName() {
        return handle().getName();
    }

    @Override
    default @NotNull BoundingBox boundingBox() {
        return handle().boundingBox();
    }
}
