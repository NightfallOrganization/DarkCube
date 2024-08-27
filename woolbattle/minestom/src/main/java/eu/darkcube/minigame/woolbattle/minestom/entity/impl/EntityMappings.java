/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity.impl;

import static net.minestom.server.entity.EntityType.*;

import java.util.HashMap;
import java.util.Map;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.minigame.woolbattle.api.entity.SimpleEntityType;
import eu.darkcube.minigame.woolbattle.minestom.MinestomWoolBattle;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import it.unimi.dsi.fastutil.Pair;
import net.minestom.server.item.ItemStack;

public class EntityMappings {
    private final Map<EntityType<?>, Entry> byCustom = new HashMap<>();
    private final Constructor defaultConstructor;

    public EntityMappings(MinestomWoolBattle woolbattle) {
        this.defaultConstructor = (type, _) -> cast(new MinestomEntityImpl(type, woolbattle));
        Constructor projectileConstructor = (type, _) -> cast(new MinestomProjectileImpl(woolbattle, type));
        Constructor itemConstructor = (_, args) -> cast(new MinestomItemEntityImpl((ItemStack) args[0], woolbattle));

        register(EntityType.PLAYER, PLAYER);
        register(EntityType.ARROW, ARROW, projectileConstructor);
        register(EntityType.SNOWBALL, SNOWBALL, projectileConstructor);
        register(EntityType.ITEM, ITEM, itemConstructor);
    }

    private Pair<net.minestom.server.entity.Entity, EntityImpl> cast(Object entity) {
        return Pair.of((net.minestom.server.entity.Entity) entity, (EntityImpl) entity);
    }

    private <T extends Entity> void register(EntityType<T> type, net.minestom.server.entity.EntityType mapping) {
        register(type, mapping, defaultConstructor);
    }

    private <T extends Entity> void register(EntityType<T> type, net.minestom.server.entity.EntityType mapping, Constructor constructor) {
        var entry = new Entry(mapping, constructor);
        byCustom.put(type, entry);
    }

    public boolean has(@NotNull EntityType<?> type) {
        return byCustom.containsKey(type);
    }

    public <T extends Entity> Pair<net.minestom.server.entity.Entity, EntityImpl> create(@NotNull EntityType<T> type, Object... args) {
        return unwrap(type).constructor.create(findBaseMapping(type), args);
    }

    @NotNull
    private Entry get(@NotNull EntityType<?> type) {
        var custom = byCustom.get(type);
        if (custom == null) throw new IllegalArgumentException("Unknown type: " + type.key().asString());
        return custom;
    }

    private Entry unwrap(EntityType<?> type) {
        var original = type;
        do {
            if (has(type)) {
                return get(type);
            }
            var simpleType = (SimpleEntityType<?>) type;
            type = simpleType.wrapping();
        } while (type != null);
        throw new IllegalArgumentException("Can't find mapping for " + original.key().asString());
    }

    public @NotNull net.minestom.server.entity.EntityType findBaseMapping(@NotNull EntityType<?> type) {
        return unwrap(type).mapping;
    }

    private record Entry(net.minestom.server.entity.EntityType mapping, Constructor constructor) {
    }

    public interface Constructor {
        Pair<net.minestom.server.entity.Entity, EntityImpl> create(net.minestom.server.entity.EntityType type, Object... args);
    }
}
