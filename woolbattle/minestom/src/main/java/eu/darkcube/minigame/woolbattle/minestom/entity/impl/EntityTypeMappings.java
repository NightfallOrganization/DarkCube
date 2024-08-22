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

import eu.darkcube.minigame.woolbattle.api.entity.EntityType;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class EntityTypeMappings {
    private final Map<EntityType<?>, net.minestom.server.entity.EntityType> byCustom = new HashMap<>();

    public EntityTypeMappings() {
        byCustom.put(EntityType.PLAYER, PLAYER);
        byCustom.put(EntityType.ARROW, ARROW);
        byCustom.put(EntityType.SNOWBALL, SNOWBALL);
        byCustom.put(EntityType.ITEM, ITEM);
    }

    public boolean has(@NotNull EntityType<?> type) {
        return byCustom.containsKey(type);
    }

    @NotNull
    public net.minestom.server.entity.EntityType get(@NotNull EntityType<?> type) {
        var custom = byCustom.get(type);
        if (custom == null) throw new IllegalArgumentException("Unknown type: " + type.key().asString());
        return custom;
    }
}
