/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import java.util.Objects;

import eu.darkcube.system.libs.net.kyori.adventure.key.Key;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public final class SimpleEntityType<T extends Entity> implements EntityType<T> {
    private final @NotNull Class<T> entityTypeClass;
    private final @NotNull Key key;
    private final @NotNull EntityTypeTest<Entity, T> test;
    private final boolean directlyInstantiable;
    private final @Nullable EntityType<?> wrapping;

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull Key key, @NotNull EntityTypeTest<Entity, T> test, boolean directlyInstantiable, @Nullable EntityType<?> wrapping) {
        EntityType.REGISTRY.register(key, this);
        this.entityTypeClass = entityTypeClass;
        this.key = key;
        this.test = test;
        this.directlyInstantiable = directlyInstantiable;
        this.wrapping = wrapping;
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key, boolean directlyInstantiable, @Nullable EntityType<?> wrapping) {
        this(entityTypeClass, EntityType.key(key), EntityTypeTest.forClass(entityTypeClass), directlyInstantiable, wrapping);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull Key key, boolean directlyInstantiable, @Nullable EntityType<?> wrapping) {
        this(entityTypeClass, key, EntityTypeTest.forClass(entityTypeClass), directlyInstantiable, wrapping);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key, @Nullable EntityType<?> wrapping) {
        this(entityTypeClass, key, true, wrapping);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull Key key, @Nullable EntityType<?> wrapping) {
        this(entityTypeClass, key, true, wrapping);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key) {
        this(entityTypeClass, key, null);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull Key key) {
        this(entityTypeClass, key, null);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull String key, boolean directlyInstantiable) {
        this(entityTypeClass, key, directlyInstantiable, null);
    }

    SimpleEntityType(@NotNull Class<T> entityTypeClass, @NotNull Key key, boolean directlyInstantiable) {
        this(entityTypeClass, key, directlyInstantiable, null);
    }

    @Override
    public @Nullable T tryCast(Entity obj) {
        return test.tryCast(obj);
    }

    @Override
    public Class<? extends Entity> getBaseClass() {
        return test.getBaseClass();
    }

    @Override
    public @NotNull Class<T> entityTypeClass() {
        return entityTypeClass;
    }

    @Override
    public @NotNull Key key() {
        return key;
    }

    public @NotNull EntityTypeTest<Entity, T> test() {
        return test;
    }

    @Override
    public boolean directlyInstantiable() {
        return directlyInstantiable;
    }

    public @Nullable EntityType<?> wrapping() {
        return wrapping;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (SimpleEntityType<?>) obj;
        return Objects.equals(this.entityTypeClass, that.entityTypeClass) && Objects.equals(this.key, that.key) && Objects.equals(this.test, that.test) && this.directlyInstantiable == that.directlyInstantiable && Objects.equals(this.wrapping, that.wrapping);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityTypeClass, key, test, directlyInstantiable, wrapping);
    }

    @Override
    public String toString() {
        return "SimpleEntityType[" + "entityTypeClass=" + entityTypeClass + ", " + "key=" + key + ", " + "test=" + test + ", " + "directlyInstantiable=" + directlyInstantiable + ", " + "wrapping=" + wrapping + ']';
    }
}
