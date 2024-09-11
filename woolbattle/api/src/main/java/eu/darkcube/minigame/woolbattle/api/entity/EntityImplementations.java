/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import java.util.function.Consumer;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;
import eu.darkcube.system.server.item.ItemBuilder;

public interface EntityImplementations {
    @NotNull
    Arrow spawnArrow(@NotNull Location location, float speed, float spread);

    @NotNull
    Arrow shootArrow(@Nullable WBUser shooter, @NotNull Location location, float speed, float spread);

    @NotNull
    <T extends Projectile> T shootProjectile(@NotNull EntityType<T> type, @Nullable WBUser user, @NotNull Location location, float speed, float spread);

    @NotNull
    <T extends ItemEntity> T spawnItem(@NotNull Location location, @NotNull Vector velocity, @NotNull ItemBuilder item, @Nullable Consumer<T> preSpawnCallback);

    @NotNull
    <T extends Entity> T spawn(@NotNull EntityType<T> type, @NotNull Location location, @NotNull Vector velocity, @Nullable Consumer<T> preSpawnCallback);
}
