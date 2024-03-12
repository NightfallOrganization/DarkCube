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

public interface EntityImplementations {
    @NotNull Projectile launchSnowball(@NotNull WBUser fromUser);

    @NotNull Projectile launchEgg(@NotNull WBUser fromUser);

    @NotNull Projectile spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread);
}
