/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.world;

import static eu.darkcube.system.kyori.wrapper.KyoriAdventureSupport.adventureSupport;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.entity.EntityTypeTest;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.minigame.woolbattle.api.world.Position;
import eu.darkcube.minigame.woolbattle.api.world.World;
import eu.darkcube.minigame.woolbattle.common.user.CommonWBUser;
import eu.darkcube.minigame.woolbattle.common.world.CommonBlock;
import eu.darkcube.minigame.woolbattle.minestom.entity.impl.EntityImpl;
import eu.darkcube.minigame.woolbattle.minestom.user.MinestomPlayer;
import eu.darkcube.system.libs.net.kyori.adventure.sound.Sound;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import net.minestom.server.instance.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MinestomWorld extends World {
    Logger LOGGER = LoggerFactory.getLogger(MinestomWorld.class);

    @NotNull
    Instance instance();

    @Nullable
    Path worldDirectory();

    @Override
    @NotNull
    CommonBlock blockAt(int x, int y, int z);

    @Override
    default void playSound(Position position, Sound sound) {
        instance().playSound(adventureSupport().convert(sound), position.x(), position.y(), position.z());
    }

    @Override
    default @Unmodifiable @NotNull List<? extends WBUser> getPlayers(@NotNull Predicate<Entity> predicate, int limit) {
        var list = new ArrayList<CommonWBUser>();
        for (var player : instance().getPlayers()) {
            if (player instanceof MinestomPlayer p) {
                var user = p.user();
                if (user == null) continue;
                if (!predicate.test(user)) continue;
                list.add(user);
                if (list.size() == limit) {
                    break;
                }
            }
        }
        return list;
    }

    @Override
    @Nullable
    default Entity getEntity(@NotNull UUID entityUUID) {
        var entity = instance().getEntityByUuid(entityUUID);
        if (entity == null) return null;
        return unwrap(entity);
    }

    @Nullable
    private static Entity unwrap(@NotNull net.minestom.server.entity.Entity entity) {
        if (!(entity instanceof EntityImpl impl)) {
            LOGGER.error("Entity {} is not spawned by woolbattle", entity);
            return null;
        }
        var handle = impl.handle();
        if (handle == null) {
            LOGGER.error("Entity handle of {} was null", entity);
            return null;
        }
        return handle;
    }

    @Override
    default void getEntities(@NotNull EntityTypeTest<Entity, ?> type, @Nullable BoundingBox box, @NotNull Predicate<Entity> filter, @NotNull List<Entity> into, int limit) {
        if (limit <= 0) return;
        var count = 0;
        for (var entity : instance().getEntities()) {
            var handle = unwrap(entity);
            if (handle == null) continue;
            var cast = type.tryCast(handle);
            if (cast == null) continue;

            if (box != null) {
                var pos = entity.getPosition();
                if (!box.contains(pos.x(), pos.y(), pos.z())) {
                    continue;
                }
            }

            if (!filter.test(cast)) {
                continue;
            }

            into.add(cast);
            count++;
            if (count == limit) {
                return;
            }
        }
    }
}
