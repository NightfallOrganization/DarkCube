/*
 * Copyright (c) 2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.minigame.woolbattle.api.world;

public record Location(World world, double x, double y, double z, float yaw, float pitch) {
    public Location(World world, double x, double y, double z) {
        this(world, x, y, z, 0, 0);
    }
}
