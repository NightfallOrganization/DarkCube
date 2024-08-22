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

public interface EntityType<T extends Entity> extends EntityTypeTest<Entity, T> {
    Registry<EntityType<?>> REGISTRY = Registry.create();
    EntityType<WBUser> PLAYER = new SimpleEntityType<>(WBUser.class, "player");
    EntityType<Entity> UNKNOWN = new SimpleEntityType<>(Entity.class, "unknown", false);
    EntityType<ItemEntity> ITEM = new SimpleEntityType<>(ItemEntity.class, "item");
    EntityType<Projectile> PROJECTILE = new SimpleEntityType<>(Projectile.class, "projectile", false);
    EntityType<Snowball> SNOWBALL = PROJECTILE.createWrapped(Snowball.class, "snowball", false);
    EntityType<Arrow> ARROW = PROJECTILE.createWrapped(Arrow.class, "projectile");

    @NotNull
    Class<T> entityTypeClass();

    @NotNull
    Key key();

    /**
     * Whether this EntityType can be directly used to create an entity.
     * This will be true for most types, but some, such as PROJECTILE, will return false.
     * To use PROJECTILE, create a wrapper entity
     *
     * @return if this type can be used to create an entity
     */
    boolean directlyInstantiable();

    @NotNull
    default <K extends T> EntityType<K> createWrapped(Class<K> cls, @NotNull String key) {
        return createWrapped(cls, key(key));
    }

    @NotNull
    default <K extends T> EntityType<K> createWrapped(Class<K> cls, @NotNull Key key) {
        return new SimpleEntityType<>(cls, key, this);
    }

    @NotNull
    default <K extends T> EntityType<K> createWrapped(Class<K> cls, @NotNull String key, boolean directlyInstantiable) {
        return createWrapped(cls, key(key), directlyInstantiable);
    }

    @NotNull
    default <K extends T> EntityType<K> createWrapped(Class<K> cls, @NotNull Key key, boolean directlyInstantiable) {
        return new SimpleEntityType<>(cls, key, directlyInstantiable, this);
    }

    static Key key(@NotNull String key) {
        return Key.key("woolbattle", key);
    }
}
