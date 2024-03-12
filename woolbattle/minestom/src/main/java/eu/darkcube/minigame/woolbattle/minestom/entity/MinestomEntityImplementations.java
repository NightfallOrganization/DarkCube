/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.minestom.entity;

import eu.darkcube.minigame.woolbattle.api.entity.EntityImplementations;
import eu.darkcube.minigame.woolbattle.api.entity.Projectile;
import eu.darkcube.minigame.woolbattle.api.user.WBUser;
import eu.darkcube.minigame.woolbattle.api.util.Vector;
import eu.darkcube.minigame.woolbattle.api.world.Location;
import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public class MinestomEntityImplementations implements EntityImplementations {
    @Override
    public @NotNull Projectile launchSnowball(@NotNull WBUser fromUser) {
        return null;
    }

    @Override
    public @NotNull Projectile launchEgg(@NotNull WBUser fromUser) {
        return null;
    }

    @Override
    public @NotNull Projectile spawnArrow(@NotNull Location location, @NotNull Vector direction, float speed, float spread) {
        return null;
    }
}
