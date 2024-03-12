/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

import eu.darkcube.system.libs.org.jetbrains.annotations.NotNull;

public record Location(@NotNull World world, double x, double y, double z, float yaw, float pitch) implements Position.Directed {
    public Location(@NotNull World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }

    public Location(@NotNull World world, @NotNull Position.Directed position) {
        this(world, position.x(), position.y(), position.z(), position.yaw(), position.pitch());
    }

    @Override
    public Location clone() {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
