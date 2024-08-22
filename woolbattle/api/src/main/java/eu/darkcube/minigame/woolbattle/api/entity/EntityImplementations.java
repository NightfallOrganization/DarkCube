/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.entity;

import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;
import eu.darkcube.system.libs.org.jetbrains.annotations.Nullable;

public interface EntityImplementations {
    @NotNull
    Arrow spawnArrow(@NotNull Location location, @NotNull Vector velocity, float speed, float spread);

    @NotNull
    Arrow shootArrow(@Nullable WBUser shooter, @NotNull Location location, @NotNull Vector velocity, float speed, float spread);

    @NotNull
    <T extends Projectile> T spawnProjectile(@NotNull EntityType<T> type, @NotNull Location location, @NotNull Vector velocity, float speed, float spread);

    @NotNull
    <T extends Projectile> T shootProjectile(@NotNull EntityType<T> type, @Nullable WBUser user, @NotNull Location location, @NotNull Vector velocity, float speed, float spread);
}
