/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import java.util.List;
import java.util.function.Predicate;

import eu.darkcube.minigame.woolbattle.api.entity.Entity;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.BoundingBox;
import eu.darkcube.system.annotations.Api;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.libs.org.jetbrains.annotations.Unmodifiable;
import eu.darkcube.system.util.data.MetaDataStorage;

@Api
public interface World {
    @Api
    @NotNull
    MetaDataStorage metadata();

    @Api
    @NotNull
    Block blockAt(int x, int y, int z);

    @Api
    void dropAt(double x, double y, double z, @NotNull ColoredWool wool, int count);

    @Api
    @Unmodifiable
    @NotNull
    List<? extends WBUser> getPlayers(@NotNull Predicate<Entity> predicate, int limit);

    void getEntities(eu.darkcube.minigame.woolbattle.common.command.arguments.entity.EntityTypeTest<Entity, ?> type, @Nullable BoundingBox box, Predicate<Entity> predicate, List<Entity> entities, int i);
}
